//jwt
var database;
var UserSchema;
var UserModel;
var authUser = require('./user').authUser;
var findByAccess = require('./user').findByAccess;
    
var jwt = require('jsonwebtoken');
var moment = require('moment');
var init = function(db, schema, model) {
	console.log('user token모듈에 있는 init 호출됨.');
	
	database = db;
	UserSchema = schema;
	UserModel = model;
}

//token 발급
var issueToken = function(req,res){
    var paramId = req.body.id || req.query.id;
    var paramPassword = req.body.password || req.query.password;
    console.log('usertoken 모듈 안에 있는 issueToken 호출됨.');
    try {
        authUser(database, paramId, paramPassword, function(err, docs) {
            if (err) {throw err;}
            
            // 조회된 레코드가 있으면 성공 응답 전송
            if (docs) {
                
                var nickname = docs[0]._doc.nickname;
                console.log('nickname: ', nickname);
                
                var accessToken = jwt.sign({paramId,nickname}, 
                    process.env.JWT_SECRET, {
                    expiresIn: '1m', // 1분
                    issuer: 'meetmeetserver',
                });
                var refreshToken = jwt.sign({}, 
                    process.env.JWT_SECRET, {
                    expiresIn: '14d', // 2주
                    issuer: 'meetmeetserver',
                });
                //expiration time도 저장하기
                //accessToken 데이터베이스에 저장
                UserModel.update({ id: paramId }, {accessToken: accessToken, updated_at: moment()},(err, output) => {
                if(err) {console.log('데이터베이스 실패'); console.log(output);}

                if(!output.n) console.log("해당 아이디 찾기 실패");
                })
                return res.json({
                  code: 200,
                  message: '토큰이 발급되었습니다',
                  accessToken,
                  refreshToken
                });
                
            } else {  // 조회된 레코드가 없는 경우 실패 응답 전송
                return res.status(401).json({
                    code: 401,
                    message: '등록되지 않은 사용자입니다. 먼저 사용자를 등록하세요.'
                });
            }
        });
        
    } catch (error) {
        console.error(error);
        return res.status(500).json({
          code: 500,
          message: '서버 에러',
        });
    }
}

//access token 만료되었을 때 refresh token 유효성 확인하고 재발급해주는 경로
//사용자가 access, refresh token 주면 refresh토큰이 유효한 토큰일 경우 access token 발급
var reissuanceToken = function(req,res){
    //나중에 사용자 access token 보내면 그 token new token으로 바꾼 후 테이블에 갱신하는 코드 만들어야 함.
    console.log('usertoken 모듈 안에 있는 reissuanceToken 호출됨.');
      try {
        req.decodedRefresh = jwt.verify(req.headers.refresh, process.env.JWT_SECRET);
        var access = req.headers.access;
          
        findByAccess(database, access, function(err, docs){
            if (err) {throw err;}
            
            if(docs){
                var paramId = docs[0]._doc.id;
                var nickname = docs[0]._doc.nickname;

                console.log('paramId: ',paramId, ',nickname: ',nickname); 

                var newAccessToken = jwt.sign({paramId,nickname}, 
                process.env.JWT_SECRET, {
                expiresIn: '1m', // 1분
                issuer: 'meetmeetserver',
                });
                
                UserModel.update({ id: paramId }, {accessToken: newAccessToken, updated_at: moment()},(err, output) => {
                if(err) {console.log('데이터베이스 실패'); console.log(output);}

                if(!output.n) console.log("해당 아이디 찾기 실패");
                })
                
                return res.status(200).json({
                code: 200,
                message: '새로운 access 토큰이 재발급 되었습니다.',
                newAccessToken
            });
            } else{
                console.log('access token에 해당되는 user찾기 실패.');
                return res.status(402).json({
                code: 402,
                message: '유효하지 않는 access 토큰 입니다.',
            });
                }
        })
        }catch (error) {
            if (error.name === 'TokenExpiredError') { // 유효기간 초과
              return res.status(419).json({
                code: 419,
                message: 'refresh 토큰이 만료되었습니다',
              })};
        return res.status(401).json({
          code: 401,
          message: '유효하지 않은 refresh토큰입니다',
        });
      }
}


//accesstoken 어떤 상태인지 인증해주는 경로
var verifyAccessToken = function(req, res){
    console.log('usertoken 모듈 안에 있는 verifyAccessToken 호출됨.');
      try {
        req.decodedAccess = jwt.verify(req.headers.access, process.env.JWT_SECRET);
   
        return res.status(200).json({
            code: 200,
            message: 'access 토큰이 인증되었습니다.',
          });
      } catch (error) {
        if (error.name === 'TokenExpiredError') { // 유효기간 초과
          return res.status(419).json({
            code: 419,
            message: 'access 토큰이 만료되었습니다',
          });
        }
        return res.status(401).json({
          code: 401,
          message: '유효하지 않은 access 토큰입니다',
        });
      }
}

//var verifyToken = function(req,res){
//    console.log('usertoken 모듈 안에 있는 verifyToken 호출됨.');
//      try {
//        req.decodedAccess = jwt.verify(req.headers.access, process.env.JWT_SECRET);
//        return res.status(200).json({
//            code: 200,
//            message: '토큰이 인증되었습니다.',
//          });
//      } catch (error) {
//        if (error.name === 'TokenExpiredError') { // 유효기간 초과
//          return res.status(419).json({
//            code: 419,
//            message: '토큰이 만료되었습니다',
//          });
//        }
//        return res.status(401).json({
//          code: 401,
//          message: '유효하지 않은 토큰입니다',
//        });
//      }
//}

////사용자가 token 테스트 해보는 함수
//var verifyToken = function(req,res){
//    console.log('usertoken 모듈 안에 있는 verifyToken 호출됨.');
//    if (req.headers.access === undefined) throw Error('API 사용 권한이 없습니다.'); 
//    req.decoder = jwt.verify(req.headers.access, process.env.JWT_SECRET); 
//    console.log(req.decoder);
//    
//    var accessToken = jwt.verify(req.headers.access); 
//    var refreshToken = jwt.verifyToken(req.headers.refresh); 
//    
//    if (accessToken === null) { 
//        if (refreshToken === null) { // case1: access token과 refresh token 모두가 만료된 경우 
//            throw Error('API 사용 권한이 없습니다.'); 
//        } else { // case2: access token은 만료됐지만, refresh token은 유효한 경우 /** * DB를 조회해서 payload에 담을 값들을 가져오는 로직 */ 
//            const newAccessToken = jwt.sign({ paramId, nickname }, process.env.JWT_SECRET, { expiresIn: '1m', issuer: 'meetmeetserver' }); 
//             return res.json({
//                  code: 200,
//                  message: 'access 토큰이 새로 발급되었습니다',
//                  newAccessToken,
//                });
//        } 
//    } else { 
//        if (refreshToken === null) { // case3: access token은 유효하지만, refresh token은 만료된 경우 
//            const newRefreshToken = jwt.sign({}, process.env.JWT_SECRET, { expiresIn: '14d', issuer: 'meetmeetserver' }); /** * DB에 새로 발급된 refresh token 삽입하는 로직 (login과 유사) */ return res.json({
//                  code: 200,
//                  message: 'refrech 토큰이 새로 발급되었습니다',
//                  newRefreshToken
//                });} 
//        else { // case4: accesss token과 refresh token 모두가 유효한 경우 
//            return res.json({
//                  code: 200,
//                  message: 'acess refresh 모두 유효합니다.',
//                  newAccessToken,
//                });
//        } 
//        } 
//}



module.exports.reissuanceToken = reissuanceToken;
module.exports.init = init;
module.exports.issueToken = issueToken;
module.exports.verifyAccessToken = verifyAccessToken;