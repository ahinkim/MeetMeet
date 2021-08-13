
/*
 * 설정 파일
 *
 * - 서버 기본 정보 (포트 등)
 * - 데이터베이스 기본 정보 (접속 URL 등)
 * - 데이터베이스 스키마
 * - 라우팅 함수
 *
 * @date 2016-11-10
 * @author Mike
 */

// 사용자 관련 모듈 불러오기
var user = require('./routes/user');

module.exports = {
	server_port: 3000,
	db_url: 'mongodb://localhost:27017/local',
	db_schemas: [
	    {file:'./user_schema', collection:'users', schemaName:'UserSchema', modelName:'UserModel'}
	],

    route_info: [
        {file: './user', path: '/process/login', method:'login',type:'post'}
        ,{file: './user', path: '/process/adduser', method:'adduser',type:'post'}
        ,{file: './user', path: '/process/validate', method:'validate',type:'post'}
        ,{file: './usertoken', path: '/token/verify', method: 'verifyToken', type: 'get'}
        ,{file: './usertoken', path: '/token/issue', method: 'issueToken', type: 'post'}
    ]
}