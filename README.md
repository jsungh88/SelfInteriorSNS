# SelfInteriorSNS
 셀프 인테리어를 하고자 하는 사람들끼리 ‘작업 노하우 및 아이템 정보를 공유’하고, 취향에 맞는 공간정보를 수집할 수 있는 ‘셀프인테리어SNS’입니다. 

## 사용 기술
* Language : java, php
* Platform : Android(java), Linux(Ubuntu)
* Web Server : Apache
* Library : Retrofit2.4.0, FCM, GooglePlaceAPI, OpenCV, Glide, Gson, JDBC,
            stepper-indicator, TedPicker
* Protocol: HTTP, TCP/IP
* Database: MySQL
* Server Hosting : AWS(EC2)


## 구현 기능
* Viewpager와 Tablayout을 사용하여 메인화면 구성
* 공간정보 글 등록시, GooglePlaceAPI사용하여 장소위치 표시
* FCM(Firebase Cloud Messaging)을 이용한 알림(팔로우,좋아요,댓글)기능
* Recyclerview사용하여 댓글/대댓글 구현
* TCP/IP 프로토콜을 사용한 오픈채팅방
* OpenCV를 이용한 프로필 변경
