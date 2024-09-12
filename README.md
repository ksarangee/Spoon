## Outline

<몰입캠프 우수작 선정>

Spoon은 대전에서 맛있는 걸 먹고 기록하고 싶은 몰입캠프 참가자들을 위한 앱입니다 😋



### 개발환경

- Android Studio (Kotlin)

 

## Team


이화여자대학교 김사랑

카이스트 이지혁

---

## Details


Spoon은 로딩 화면을 시작으로 Contacts, Food Gallery, Food Log 탭으로 구성되어 있습니다.

### Intro: Splash

Splash screen을 활용한 Spoon 로딩 화면

https://github.com/ksarangee/Spoon_madcampW1/assets/155128117/0bdc3805-75a9-43da-a6af-de3be1685ddd


### Tab 1: Contacts

대전 맛집을 함께 탐색할 몰입캠프 구성원의 연락처를 저장하고 바로 연락하기 위한 탭 

- **연락처 동기화 | 목록 보기 | 검색**
    - 연락처 동기화를 누르면 JSON 데이터 형식을 이용하여 RecyclerView로 연락처 목록을 볼 수 있다. 각 프로필은 CardView로 만들었으며, SearchView를 이용한 검색 기능을 통해 사용자가 프로필 검색을 할 수 있게 했다.

https://github.com/ksarangee/Spoon_madcampW1/assets/155128117/e0ac39f6-836a-4c06-9701-9b2b3805c685


- **연락처 추가**
    - 오른쪽 하단에 있는 + 버튼 클릭시 연락처를 추가할 수 있으며, 프로필의 사진은 미모티콘 혹은 갤러리에 있는 이미지를 선택할 수 있다. 이름과 번호 입력 후 Add를 누르면 추가가 된다.

https://github.com/ksarangee/Spoon_madcampW1/assets/155128117/5f756955-5927-4647-ae6d-8480038ed2dc


- **연락처 별표 표시 | 삭제 | 전화 연결**
    - 프로필에 있는 별표를 누르면 해당 프로필은 연락처 목록 상단에 표시된다. 휴지통 아이콘을 누르면 해당 프로필은 목록에서 삭제되며, 프로필의 전화번호를 누를 시 전화 연결이 된다.

https://github.com/ksarangee/Spoon_madcampW1/assets/155128117/3e25b2a0-88cc-4a05-81d5-5c65de9411bc


### Tab 2: Food Gallery

맛집에서 찍은 사진들을 둘러보며 자신만의 맛집 앨범을 만드는 탭

- **이미지 불러오기 | 카메라**
    - 갤러리 탭 또한 RecyclerView를 활용하여 사진들이 보여지게 했고, Glide 라이브러리를 사용하였다. 오른쪽 하단에 있는 + 버튼 클릭시 이미지를 불러올 수 있으며, 이미지를 꾹 누르면 여러장을 선택해 불러올 수 있다. 왼쪽 하단에 있는 카메라 버튼을 누르면 카메라와 연동이 되고, 찍은 사진은 갤러리탭에서 볼 수 있다.

https://github.com/ksarangee/Spoon_madcampW1/assets/155128117/543a15c4-42b8-44d9-9a34-2a76c323b2ad


- **사진 키우기 및 줌인 | 삭제**
    - toggleVisibility 함수를 활용하여 이미지를 클릭하면 하단에 있는 카메라 버튼과 + 버튼은 사라지고, 휴지통 버튼과 돋보기 버튼이 나타나도록 구현하였다. 돋보기 버튼을 누르면 클릭한 이미지를 크게 볼 수 있으며, 줌인도 할 수 있다. 휴지통 버튼을 누르면 해당 이미지가 삭제된다.

https://github.com/ksarangee/Spoon_madcampW1/assets/155128117/13c97e14-e0d8-4edf-b022-9fff420ab3ab


### Tab 3: Food Log

자신이 다녀온 맛집을 캘린더에 기록하는 탭

- **맛집 기록 추가 | 식당 위치 검색 및 등록 | 삭제**
    - MaterialView 캘린더를 활용하여 날짜를 선택하면 그 날에 간 맛집을 기록할 수 있다. 대표 사진 한장, 한 줄평, 그리고 맛집의 위치를 등록할 수 있다.
    - 네이버 맵 API를 활용해 위치 버튼을 누르면 지도뷰가 뜨도록 하였고, 카카오 REST API를 통해 주변 식당과 식당 검색을 통해 위치 등록을 할 수 있도록 구현하였다. 식당 검색을 했을 때 식당의 전화번호를 누르면 전화창으로 이동하게 된다.
    - 맛집 기록을 추가하면 해당 날짜 아래 작은 점이 생기고, 휴지통 아이콘을 누르면 해당 기록이 삭제된다.

https://github.com/ksarangee/Spoon_madcampW1/assets/155128117/67fdee3a-88a2-456c-8f3e-c38078d7e14c


