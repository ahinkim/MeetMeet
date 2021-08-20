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

//다이어리 등록 요청
var postDiary = function(req, res) {
	console.log('diary모듈 안에 있는 postDiary 호출됨.');
    
    var diary = req.body.diary || req.query.diary;
    var access = req.headers.access;
   // var objectId;
    
    console.log('요청 파라미터 : ' + diary + ', ' + access);
    
    // 데이터베이스 객체가 초기화된 경우, addUser 함수 호출하여 사용자 추가
	if (database) {
           findByAccess(database,access, function(err, results) {
            if(err){ 
                res.status(500).json({
                code: 500,
                message: '서버 에러.',
                });
                throw err;         
            }
			
            if( results != null && results.length>0){
                var objectId = results[0]._doc._id;
                addDiary(database, objectId, diary, function(err, diaryId) {
                    if (err) {
                        res.status(500).json({
                            code: 500,
                            message: '서버 에러.',
                        });
                        throw err;
                    }

                    // 결과 객체 있으면 성공 응답 전송
                    if (diaryId) {

                        res.status(200).json({
                            code: 200,
                            diary_id: diaryId,
                            message: '다이어리 등록 성공.',
                        });

                    } else {  // 결과 객체가 없으면 실패 응답 전송

                            res.status(500).json({
                            code: 500,
                            message: '다이어리 등록 실패.',
                        });
                    }
		      });
//                        var result = DiaryModel.populate(diary, { path: 'writer' });
//    
//                res.status(200).json(result);
            }else{
                res.status(402).json({
                    code: 402,
                    message: 'accessToken에 해당하는 사용자가 없습니다.',
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


//다이어리 수정 요청
var putDiary = function(req, res) {
	console.log('diary모듈 안에 있는 putDiary 호출됨.');
    
    var diary_id = req.query.id;//req.params.diary_id;
    var diary = req.body.diary || req.query.diary;
    
    console.log('요청 파라미터 : ' + diary_id + ', ' + diary);
    
    // 데이터베이스 객체가 초기화된 경우, addUser 함수 호출하여 사용자 추가
	if (database) {
           modify(database, diary_id, diary, function(err, results) {
            if(err){ 
                res.status(500).json({
                code: 500,
                message: '서버 에러.',
                });
                throw err;         
            }
			
            if(results){
                res.status(200).json({
                code: 200,
                message: '다이어리 수정 완료',
                });
            }else{
                res.status(402).json({
                code: 402,
                message: 'diary_id에 해당하는 diary가 없습니다.',
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

//다이어리 삭제 요청
var deleteDiary = function(req, res) {
	console.log('diary모듈 안에 있는 deleteDiary 호출됨.');
    
    var diary_id = req.query.id;
    
    console.log('요청 파라미터 : '  + diary_id);
    
    // 데이터베이스 객체가 초기화된 경우, addUser 함수 호출하여 사용자 추가
	if (database) {
           deletediary(database, diary_id, function(err, results) {
            if(err){ 
                res.status(500).json({
                code: 500,
                message: '서버 에러.',
                });
                throw err;         
            }
			
            if(results){
                res.status(200).json({
                code: 200,
                message: '다이어리 삭제 완료',
                });
            }else{
                res.status(402).json({
                code: 402,
                message: 'diary_id에 해당하는 diary가 없습니다.',
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

//다이어리를 추가하는 함수
var addDiary = function(database, objectId, contents, callback) {
	console.log('addDiary 호출됨 : ' + objectId + ', ' + contents );

    DiaryModel.find({"writer":objectId}, function(err, results) {

    if (err) {  // 에러 발생 시 콜백 함수를 호출하면서 에러 객체 전달
        callback(err, null);
        return;
    }

    var diary = new DiaryModel({"writer":objectId, "diary": contents});
    var result = DiaryModel.populate(diary, { path: 'writer' });

    diary.save(function(err, addedDiary) {
        if (err) {
            callback(err, null);
            return;
        }

        console.log("다이어리 추가함.");
        callback(null, diary._doc._id);

    });

    })
};

//다이어리를 수정하는 함수
var modify = function(database, id, contents, callback) {
	console.log('modify 호출됨 : ' + id + ', ' + contents );

    DiaryModel.update({"_id": id}, {"diary": contents, "updated_At": moment()}, (err, output) => { //updatedat추가하기
        if (err) {  // 에러 발생 시 콜백 함수를 호출하면서 에러 객체 전달
            callback(err, null);
            return;
        }
        if(!output.n){ 
            console.log("해당 다이어리 아이디 찾기 실패");
            callback(null,null);
        }
        
        callback(null,true);
        
    })
};

//다이어리를 삭제하는 함수
var deletediary = function(database, diaryId, callback) {
	console.log('deletediary 호출됨 : '+ diaryId );

    DiaryModel.remove({"_id": diaryId}, (err, output) => { //updatedat추가하기
        if (err) {  // 에러 발생 시 콜백 함수를 호출하면서 에러 객체 전달
            callback(err, null);
            return;
        }
        console.dir(output);
        if(output.result.n){ 
            console.log("해당 다이어리 아이디 찾기 성공");
            callback(null,true);
        }
        else{
            console.log("해당 다이어리 아이디 찾기 실패");
            callback(null,null);
        }
        
    });
};


module.exports.init = init;
module.exports.postDiary = postDiary;
module.exports.putDiary = putDiary;
module.exports.deleteDiary = deleteDiary;