/**
 * 데이터베이스 사용하기
 * 
 * mongoose로 데이터베이스 다루기
 * 사용자 조회, 사용자 추가
 *
 * 웹브라우저에서 아래 주소의 페이지를 열고 웹페이지에서 요청
 *    http://localhost:3000/public/login.html
 *    http://localhost:3000/public/adduser.html
 *
 * @date 2016-11-10
 * @author Mike
 */

// Express 기본 모듈 불러오기
var express = require('express')
  , http = require('http')
  , path = require('path');

// Express의 미들웨어 불러오기
var bodyParser = require('body-parser')
  , cookieParser = require('cookie-parser')
  , static = require('serve-static')
  , errorHandler = require('errorhandler')
  , dotenv = require('dotenv')
  , morgan = require('morgan');

// 에러 핸들러 모듈 사용
var expressErrorHandler = require('express-error-handler');

// Session 미들웨어 불러오기
var expressSession = require('express-session');
 
// mongoose 모듈 사용
var mongoose = require('mongoose');

crypto = require('crypto');

// 익스프레스 객체 생성
var app = express();

var config = require('./config');

app.use(morgan('dev'));

dotenv.config();
//==== 서버 변수 설정 및  static으로 [public]폴더 설정 ====//
console.log('config.server_port: %d', config.server_port);
app.set('port', process.env.PORT || 3000);

// body-parser를 이용해 application/x-www-form-urlencoded 파싱
app.use(bodyParser.urlencoded({ extended: true }))

// body-parser를 이용해 application/json 파싱
app.use(bodyParser.json())

// public 폴더를 static으로 오픈
app.use('/public', static(path.join(__dirname, 'public')));
 
// cookie-parser 설정
app.use(cookieParser());

// 세션 설정
app.use(expressSession({
	secret:'my key',
	resave:true,
	saveUninitialized:true
}));



//===== 데이터베이스 연결 =====//

// 데이터베이스 객체를 위한 변수 선언
var database;

// 데이터베이스 스키마 객체를 위한 변수 선언
var UserSchema;

// 데이터베이스 모델 객체를 위한 변수 선언
var UserModel;

var DiarySchema;

var DiaryModel;
//데이터베이스에 연결
function connectDB() {
	// 데이터베이스 연결 정보
	var databaseUrl = 'mongodb://localhost:27017/meetmeet';
	 
	// 데이터베이스 연결
    console.log('데이터베이스 연결을 시도합니다.');
    mongoose.Promise = global.Promise;  // mongoose의 Promise 객체는 global의 Promise 객체 사용하도록 함
	mongoose.connect(databaseUrl);
	database = mongoose.connection;
	
	database.on('error', console.error.bind(console, 'mongoose connection error.'));	
	database.on('open', function () {
		console.log('데이터베이스에 연결되었습니다. : ' + databaseUrl);
        
        //user 스키마 및 모델 객체 생성
        createUserSchema();
	
    });
    
    // 연결 끊어졌을 때 5초 후 재연결
	database.on('disconnected', function() {
        console.log('연결이 끊어졌습니다. 5초 후 재연결합니다.');
        setInterval(connectDB, 5000);
    });
}

var user = require('./routes/user');
var usertoken = require('./routes/usertoken');
var diary = require('./routes/diary');
var diary_schema = require('./database/diary_schema');

//user 스키마 및 모델 객체 생성
function createUserSchema(){

    //user_schema.js 모듈 불러오기
    UserSchema = require('./database/user_schema').createSchema(mongoose);

    // UserModel 모델 정의
    UserModel = mongoose.model("users", UserSchema);
    console.log('UserModel 정의함.');
    
//    diary_schema.init(UserModel);
    DiarySchema = require('./database/diary_schema').createSchema(mongoose);
    DiaryModel = mongoose.model("diaries", DiarySchema);
    console.log('DiaryModel 정의함.');
    //init 호출
    user.init(database, UserSchema, UserModel);
    usertoken.init(database, UserSchema, UserModel);
    diary.init(database, DiarySchema, DiaryModel, UserModel);
}	



//===== 라우팅 함수 등록 =====//

// 라우터 객체 참조
var router = express.Router();

var route_loader = require('./routes/route_loader');
route_loader.init(app, router);


//var database = require('./database/database');
//database.init(app,config);

app.use( expressErrorHandler.httpError(404) );
app.use( errorHandler );


//===== 서버 시작 =====//

// 프로세스 종료 시에 데이터베이스 연결 해제
process.on('SIGTERM', function () {
    console.log("프로세스가 종료됩니다.");
    app.close();
});

app.on('close', function () {
	console.log("Express 서버 객체가 종료됩니다.");
	if (database) {
		database.close();
	}
});

// Express 서버 시작
http.createServer(app).listen(app.get('port'), function(){
  console.log('서버가 시작되었습니다. 포트 : ' + app.get('port'));

  // 데이터베이스 연결을 위한 함수 호출
  connectDB();
   
});

