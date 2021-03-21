<a name="top">

# 부동산/신용 투자 서비스 API 개발

</a>

## 목차

#### [1. 프로젝트 설명](#about_project)
#### [2. 기능](#functions)
#### [3. 기술 스택](#stack)
#### [4. 외부 라이브러리](#dependency)
#### [5. 실행 가이드](#install_guide)
#### [6. 테스트 가이드](#test_guide)
#### [7. 기능 요구 사항](#requirement)


</br>

<a name="about_project">

### 1. 프로젝트 설명

</a>

 > 부동산/신용 투자 서비스 API
 
[맨 위로 가기](#top)
</br>

</br>

<a name="functions">

### 2. 기능
 - 전체 투자 상품 조회
 - 투자하기
 - 나의 투자 상품 조회
 - 사용자 등록
 - 투자 상품 등록
 
</a>


[맨 위로 가기](#top)
</br>

</br>

<a name="stack">

### 3. 기술 스택

</a>

##### 기술 스택
 - Spring Boot
 - JAVA 1.8
 - H2 DB
 - Spring JPA
 - Gradle

[맨 위로 가기](#top)
</br>

</br>

<a name="dependency">

### 4. 의존성
</a>

 > dependency
 - spring-web : 톰캣, webmvc 등
 - mysql-connector : DB(Main)
 - h2 database : InMemory DB(Test)
 - spring-data-jpa : jdbc, jpa
 - devtools : 개발할 때 유용한 라이브러리
 - lombok : Java 코드 컴파일 시 자동으로 추가 메서드 생성
 - spring-boot-starter-test : 테스트 진행

[맨 위로 가기](#top)
</br>

</br>

<a name="install_guide">

### 5. 실행 가이드

</a>

실행방법

##### Properties
1) 환경변수 세팅

프로그램 실행을 위해 DB 접속을 위한 환경변수 세팅이 필요합니다.
세팅에 필요한 환경변수는 다음과 같습니다.
DATASOURCE_URL : DB 접속 URL
DATASOURCE_USERNAME : DB 접속 아이디
DATASOURCE_PASSWORD : DB 접속 패스워드

환경변수 설정 이후 다음 명령을 통해 jar 파일을 실행할 수 있습니다.
```
gradlew build		# 빌드
cd /build/libs/		# 디렉토리 이동
java -jar codeTest.jar	# 프로그램 실행
```

[맨 위로 가기](#top)
</br>

<a name="test_guide">

### 6. 테스트 가이드

</a>
 
 > DB 접속 정보
 - 계정 : `sa/(비밀번호 없음)`
 - 콘솔 경로 : <http://localhost:8080/h2_db>
 
 > API 정보
 - URI
   ```
	: GET /kakaopay/investments ( 전체 투자 상품 조회 )
	: POST /kakaopay/orders ( 투자 하기 )			{HTTP Header에 X-USER-ID 적재, @RequestBody OrderRequest request} 
	: GET /kakaopay/user/investments ( 나의 투자 상품 조회 )	{HTTP Header에 X-USER-ID 적재}
	: POST /kakaopay/users ( 사용자 등록 )			{HTTP Header에 X-USER-ID 적재}
	: POST /kakaopay/investments ( 투자 상품 등록 )		{@RequestBody InvestmentRequest request} 
	: GET /kakaopay/sample ( 샘플 생성 )
   ```
 - @RequestBody
   ```
	: OrderRequest -> {Long productId, Integer amount}
	: InvestmentRequest -> {String title, Integer investmentAmount, Date startedAt, Date finishedAt}
   ```
 
[맨 위로 가기](#top)
</br>

<a name="requirement">

### 7. 기능 요구 사항

</a>

 > 사용자와 투자상품의 연관관계를 다대다로 정의하기 위해 복합키 역할을 하는 Order 엔티티 클래스를 사용하였습니다.
 > 사용자 또는 투자상품 최초 생성 시 연관관계가 없지만 특정 상품에 사용자가 투자를 하는 순간 Order가 생성되고 관계를 매핑하도록 설계되었습니다.
 > 다수 인스턴스의 동작을 위해 Mysql DB를 사용하고, 테스트 시에는 H2 DB를 사용하고자 하였습니다.

1. 전체 투자 상품 조회 API
 - DB에서 CURRENT_TIMESTAMP를 기준으로 한 상품 모집기간 내의 상품만 응답하였습니다.

2. 투자하기 API 
 - 총 투자모집 금액을 기준으로 투자 가능상태를 판단하고 sold-out 상태를 응답하기 위해 에러 코드를 정의하였습니다.
 - 투자 진행 시 기존에 해당 상품에 투자한 이력이 있는 지 확인 후 Order를 생성합니다.

3. 나의 투자상품 조회 API
 - 사용자 엔티티에 매핑된 Order를 통해 투자한 모든 상품을 조회할 수 있습니다.


