var database;
//var UserSchema;
var DiarySchema;
//var UserModel;
var DiaryModel;
var UserModel;
var findByAccess = require('./user').findByAccess;
var moment = require('moment');

// 데이터베이스 객체, 스키마 객체, 모델 객체를 이 모듈에서 사용할 수 있도록 전달함
var init = function(db, diarySchema, diaryModel, userModel) {
	console.log('diary모듈에 있는 init 호출됨.');
	
	database = db;
    DiarySchema = diarySchema;
    DiaryModel = diaryModel;
    UserModel = userModel;
}

//닉네임 수정 요청
var putNickname = function(req, res) {
	console.log('mypage모듈 안에 있는 putNickname 호출됨.');
    
    var accessToken = req.headers.access;
    var nickname = req.body.nickname;
    
    console.log('요청 파라미터 : ' + nickname + ', ' + accessToken);
    
    // 데이터베이스 객체가 초기화된 경우, addUser 함수 호출하여 사용자 추가
	if (database) {
           modify(database, accessToken, nickname, function(err, results) {
            if(err){ 
                res.status(500).json({
                code: 500,
                message: '서버 에러.'
                });
                throw err;         
            }
			
            if( results){
                res.status(200).json({
                code: 200,
                message: '닉네임 수정 완료'
                });
            }else{
                res.status(401).json({
                code: 401,
                message: '유효하지 않은 access 토큰입니다.'
                });
                return;
            }
		});
        
	} else {  // 데이터베이스 객체가 초기화되지 않은 경우 실패 응답 전송
		//res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
		res.status(500);
        res.write('데이터베이스 연결 실패');
		res.end();
	}
	
}
//다이어리를 수정하는 함수
var modify = function(database, access, nick, callback) {
	console.log('modify 호출됨 : ' + access + ', ' + nick);

    UserModel.update({"accessToken": access}, {"nickname": nick, "updated_At": moment()}, (err, output) => { //updatedat추가하기
        if (err) {  // 에러 발생 시 콜백 함수를 호출하면서 에러 객체 전달
            callback(err, null);
            return;
        }
        if(!output.n){ 
            console.log("해당 access Token 찾기 실패");
            callback(null,false);
            return;
        }
        
        callback(null,true);
        
    })
};

module.exports.init = init;
module.exports.putNickname = putNickname;