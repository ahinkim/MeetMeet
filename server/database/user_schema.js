/**
 * 데이터베이스 스키마를 정의하는 모듈
 *
 * @date 2016-11-10
 * @author Mike
 */

var crypto = require('crypto');

var Schema = {};

Schema.createSchema = function(mongoose) {
	
	// 스키마 정의
		UserSchema = mongoose.Schema({
			id: {type:String, required:true, unique:true},
            password: {type: String, required:true},
            nickname: {type:String, index:'hashed'},
            created_at: {type:Date,index:{unique:false},'default':Date.now},
            updated_at: {type:Date,index:{unique:false},'default':Date.now}
		});
        
        //스키마에 static 메소드 추가
        UserSchema.static('findById', function(id,callback){
            return this.find({id: id}, callback);
        });
        
        UserSchema.static('findAll', function(callback){
            return this.find({ }, callback);
        });
        
		console.log('UserSchema 정의함.');
	
    
	return UserSchema;
};

// module.exports에 UserSchema 객체 직접 할당
module.exports = Schema;

