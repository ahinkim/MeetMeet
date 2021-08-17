/*
 * 사용자 정보 처리 모듈
 * 데이터베이스 관련 객체들을 init() 메소드로 설정
 *
 * @date 2016-11-10
 * @author Mike
 */

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





// module.exports 객체에 속성으로 추가

module.exports.init = init;
module.exports.login = login;
module.exports.adduser = adduser;
module.exports.validate = validate;
module.exports.authUser = authUser;
module.exports.findByAccess = findByAccess;

