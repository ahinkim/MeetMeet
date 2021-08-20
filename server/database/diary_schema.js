/**
 * 데이터베이스 스키마를 정의하는 모듈
 *
 * @date 2016-11-10
 * @author Mike
 */

var crypto = require('crypto');
//var autoIncrement = require('mongoose-auto-increment');
var UserModel;
var Schema = {};

//var { Types: { ObjectId } } = Schema;

//var init = function(model) {
//	console.log('diary_schema 모듈에 있는 init 호출됨.');
//
//	UserModel = model;
//}

Schema.createSchema = function(mongoose) {
//	autoIncrement.initialize(mongoose.connection);
	// 스키마 정의
		 DiarySchema = mongoose.Schema({					
	        writer: {type: mongoose.Schema.Types.ObjectId, required: true, ref: 'users'}, //type: mongoose.Schema.Types.ObjectId, ref: 'UserModel'
            diary: {
                type: String,
                required: true
            },
            created_At: {
                type: Date,
                default: Date.now
            },
            updated_At: {
                type: Date,
                default: Date.now
            }
		});
    
        
		console.log('DiarySchema 정의함.');
	
    
	return DiarySchema;
};


// module.exports에 UserSchema 객체 직접 할당
module.exports = Schema;
//module.exports.init = init;
