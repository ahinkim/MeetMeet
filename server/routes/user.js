var database;
var UserSchema;
var UserModel;

// 데이터베이스 객체, 스키마 객체, 모델 객체를 이 모듈에서 사용할 수 있도록 전달함
var init = function(db, schema, model) {
	console.log('user모듈에 있는 init 호출됨.');
	
	database = db;
	UserSchema = schema;
	UserModel = model;
}

var login = function(req, res) {
	console.log('user모듈 안에 있는 login 호출됨.');

	// 요청 파라미터 확인
    var paramId = req.body.id || req.query.id;
    var paramPassword = req.body.password || req.query.password;
	
    console.log('요청 파라미터 : ' + paramId + ', ' + paramPassword);
	
    // 데이터베이스 객체가 초기화된 경우, authUser 함수 호출하여 사용자 인증
	if (database) {
		authUser(database, paramId, paramPassword, function(err, docs) {
			if (err) {throw err;}
			
            // 조회된 레코드가 있으면 성공 응답 전송
			if (docs) {
				console.dir(docs);

                // 조회 결과에서 사용자 이름 확인
				res.status(200).json({ success: true });
				res.end();
			
			} else {  // 조회된 레코드가 없는 경우 실패 응답 전송
				res.status(200).json({ success: false });
				res.end();
			}
		});
	} else {  // 데이터베이스 객체가 초기화되지 않은 경우 실패 응답 전송
		res.status(200);
        res.write('데이터베이스 연결 실패');
        res.end();
	}
	
}

var adduser = function(req, res) {
	console.log('user모듈 안에 있는 adduser 호출됨.');

    var paramId = req.body.id || req.query.id;
    var paramPassword = req.body.password || req.query.password;
	var paramNickname = req.body.nickname || req.query.nickname;
    
    console.log('요청 파라미터 : ' + paramId + ', ' + paramPassword );
    
    // 데이터베이스 객체가 초기화된 경우, addUser 함수 호출하여 사용자 추가
	if (database) {
		addUser(database, paramId, paramPassword, paramNickname, function(err, addedUser) {
			if (err) {
                res.status(400);
                throw err;
            }
			
            // 결과 객체 있으면 성공 응답 전송
			if (addedUser) {
                
				console.dir(addedUser);
                res.status(200);
                res.write("사용자 등록 성공");
				res.end();
                
			} else {  // 결과 객체가 없으면 실패 응답 전송
                
                res.status(400);
                res.write("사용자 등록 실패");
				res.end();
			}
		});
	} else {  // 데이터베이스 객체가 초기화되지 않은 경우 실패 응답 전송
		//res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
		res.status(200);
        res.write('데이터베이스 연결 실패');
		res.end();
	}
	
}

var validate = function(req, res) {
	console.log('user 모듈 안에 있는 validate 호출됨.');

    var paramId = req.body.id || req.query.id;

    console.log('요청 파라미터 : ' + paramId );
    
    // 데이터베이스 객체가 초기화된 경우, addUser 함수 호출하여 사용자 추가
	if (database) {
        validateUser(database, paramId, function(err, result) {
			if (err) { 
                res.status(400);
                throw err;
            }
			
            // 결과 객체 있으면 성공 응답 전송
			if (result) {
                res.status(200).json({ success: false });
				res.end();
                
			} else {  // 결과 객체가 없으면 실패 응답 전송
                
                res.status(200).json({ success: true });
				res.end();
			}
		});
	} else {  // 데이터베이스 객체가 초기화되지 않은 경우 실패 응답 전송
		//res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
		res.status(200);
        res.write('데이터베이스 연결 실패');
		res.end();
	}
	
}

var logout = function(req,res){
    console.log('user 모듈 안에 있는 logout 호출됨.');
    
    var access = req.headers.access;
    
    UserModel.update({"accessToken": access},{"accessToken": null}, function(err,results){
        if(err){    //데이터베이스 에러(서버 에러 처리)
            res.status(500);
            throw err;
        }
        
        if(results.n){
            res.status(200).json({
                code: 200,
                message:'logout 처리 되었습니다.'
            });
        } else{
            res.status(401).json({
                code: 401,
                message: '유효하지 않은 access 토큰입니다.'
            });
        }
    });
}

//회원탈퇴 함수
var deleteUser = function(req, res) {
	console.log('diary모듈 안에 있는 deleteUser 호출됨.');
    
    var accessToken = req.headers.access;
    
    console.log('요청 파라미터 : '  + accessToken);
    
    // 데이터베이스 객체가 초기화된 경우, addUser 함수 호출하여 사용자 추가
	if (database) {
           findByAccess(database, accessToken, function(err, results) {
                if(err){ 
                    res.status(500).json({
                    code: 500,
                    message: '서버 에러.',
                    });
                    throw err;         
                }

                if(results!=null && results.length>0){ //results != null && results.length>0

                    var objectId = results[0]._doc._id;

                    secession(database, objectId, function(err, results) {
                        if(err){ 
                            res.status(500).json({
                            code: 500,
                            message: '서버 에러.',
                            });
                            throw err;         
                        }
                        
                        if( results ){
                            res.status(200).json({
                                code: 200,
                                message: '회원 탈퇴 성공'
                            });

                        }else{
                            res.status(402).json({
                                code: 402,
                                message: 'accessToken에 해당하는 사용자가 없습니다.',
                            });
                            return;
                        }
                    });
                }else{
                    res.status(402).json({
                    code: 402,
                    message: 'accessToken에 해당하는 사용자가 없습니다.',
                    });
                    return;
                }
		});
        
    }else {  // 데이터베이스 객체가 초기화되지 않은 경우 실패 응답 전송
            //res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
            res.status(500);
            res.write('데이터베이스 연결 실패');
            res.end();
        }
	
}

// 사용자를 인증하는 함수
var authUser = function(database, id, password, callback) {
	console.log('authUser 호출됨.');
	
    //1.아이디를 사용해 검색
    UserModel.findById(id,function(err,results){
        if(err){
            callback(err,null);
            return;
        }
        
        console.log('아이디 [%s]로 사용자 검색결과', id);
        console.dir(results);
        
        if (results.length > 0) { 
	    	console.log('아이디와 일치하는 사용자 찾음.');
	 
    
            //2. 비밀번호 확인
            if(results[0]._doc.password == password){
                console.log('비밀번호 일치함.');
                callback(null, results);
            }else{
                console.log('비밀번호 일치하지 않음.');
                callback(null, null);
            }
        }else{
            console.log("아이디와 일치하는 사용자를 찾지 못함.");
            callback(null,null);
        }
        
    });
}


//사용자를 추가하는 함수
var addUser = function(database, id, password,nickname, callback) {
	console.log('addUser 호출됨 : ' + id + ', ' + password + ', '+ nickname );

    UserModel.find({"id":id}, function(err, results) {

    if (err) {  // 에러 발생 시 콜백 함수를 호출하면서 에러 객체 전달
        callback(err, null);
        return;
    }

    // UserModel 인스턴스 생성
    var user = new UserModel({"id":id, "password":password, "nickname":nickname, "accessToken": null});
   
    // save()로 저장 : 저장 성공 시 addedUser 객체가 파라미터로 전달됨
    user.save(function(err, addedUser) {
    if (err) {
        callback(err, null);
        return;
    }

    console.log("사용자 데이터 추가함.");
    callback(null, addedUser);

        });
    })
};



//사용자 중복확인 하는 함수
var validateUser = function(database, id, callback) {
	console.log('validateUser 호출됨 : ' + id );
	
    UserModel.find({"id":id}, function(err, results) {

        if (err) {  // 에러 발생 시 콜백 함수를 호출하면서 에러 객체 전달
            callback(err, null);
            return;
        }

        if (results.length > 0) { 
            // 조회한 레코드가 있는 경우 콜백 함수를 호출하면서 조회 결과 전달
                console.log('아이디 중복');
                callback(null, true);
                return;

            } else{
                console.log('아이디 중복되지 않음');
                callback(null, null);
            }
    })
};

//access token이 맞는 user 반환
var findByAccess = function(database, access, callback) {
	console.log('findByAccess 호출됨 : ' + access );
	
    UserModel.find({"accessToken": access}, function(err, results) {

        if (err) {  // 에러 발생 시 콜백 함수를 호출하면서 에러 객체 전달
            callback(err, null);
            return;
        }

        if (results.length > 0) { 
            // 조회한 레코드가 있는 경우 콜백 함수를 호출하면서 조회 결과 전달
                console.log('해당 user 찾음.');
                callback(null, results);
                return;

            } else{
                console.log('해당되는 user 없음.');
                callback(null, null);
            }
    })
};

////필요한 경우 req.app.get('database')로 참조 가능 : 이 파일에서는 사용하지 않음
//var checkDatabase = function(req) {
//	if (!database) {
//		console.log('user 모듈에서 데이터베이스 객체를 참조합니다.');
//		
//		database = req.app.get('database');
//		UserSchema = req.app.get('UserSchema');
//		UserModel = req.app.get('UserModel');
//	} else {
//		console.log('user 모듈에서 데이터베이스 객체를 이미 참조했습니다.');
//	}
//}


//회원탈퇴 함수
var secession = function(database, objectId, callback) {
	console.log('secession 호출됨 : '+ objectId );

    UserModel.remove({"_id": objectId}, (err, output) => { //updatedat추가하기
        if (err) {  // 에러 발생 시 콜백 함수를 호출하면서 에러 객체 전달
            callback(err, null);
            return;
        }
        if(output.result.n){ 
            var deleteDiaryById = require('./diary').deleteDiaryById;
            console.log("해당 사용자 토큰 찾기 성공");
            deleteDiaryById(database,objectId, function(err, results){
                if(err){
                    callback(err,null);
                }else{
                    callback(null,true);
                }
                
            });  
        }
        else{
            console.log("해당 사용자 토큰 찾기 실패");
            callback(null,null);
        }
        
    });

};


// module.exports 객체에 속성으로 추가

module.exports.init = init;
module.exports.login = login;
module.exports.logout = logout;
module.exports.adduser = adduser;
module.exports.validate = validate;
module.exports.authUser = authUser;
module.exports.findByAccess = findByAccess;
module.exports.deleteUser = deleteUser;

