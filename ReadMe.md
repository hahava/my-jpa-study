## 1일차 정리

- 엔터티 매니저 팩토리는 애플리케이션에서 하나만 생성해서 공유한다.
- 엔터티 매니저는 공유할 수 없다.
- jpa의 모든 작동은 `Transaction` 안에서 처리한다.
- `commit` 를 하기전까진 실제로 쿼리를 수행하지 않는다.
- entity를 통해 어떤 객체를 관리하면 변경시에 update 쿼리를 수행하게 된다.
- 통계 또는 다양한 조건등의 복잡한 쿼리는 `jpql` 등을 통해 처리한다.
- 현업에선 join이 많이 사용되는데 쿼리는 안 사용할 수는 없다. 
- jpql 은 데이터베이스 테이블을 알지 못한다.

#### 주의
- gradle로 작성할 경우 `<class></class>`를 사용해야 정상적으로 entity를 인식할 수 있다.

## 2일차 정리

- 요청당 entityManager를 factory로 부터 생성하여 사용한다.
- entity 당 하나의 커넥션을 사용한다 라는 개념으로 이해하면 얼추 맞다.
- jpa를 접하면 `영속성 컨텍스트` 를 많이 들을 것이다.
    - `entity.persist()` 단순히 db에 저장 이렇게 이해하면 안된다. 영속성 컨텍스트에 저장하는 것이다.
    - 논리적인 개념으로 눈에 보이지 않는다.
- entity의 생명주기
    - 비영속 : 객체가 생성되기만 하고 컨텍스트와 아무런 관계가 없음.
    - 영속 : `entity.persist(obj)` 를 통해 등록된 상태를 의미
    - 준영속 
    - 삭제
- 굳이 영속성 컨텍스트가 존재하는 이유?
    - 1차 캐시 : 데이터베이스 한 트랜잭션 내부에서만 사용. redis등은 jpa에서 2차캐시로 부름
    - 동일성 보장 -> `==` 비교시 true
    - 트랜잭션을 지원하는 쓰기 지연
    - 변경 감지
    - 지연 로딩
- 쓰기 지연 sql 저장소
    - `persist()` 호출 시 1차 캐시와 쓰기 지연 SQL 저장소에 저장
    - `commit()` 호출 시 해당 쿼리가 flush 되면서 수행 후 데이터베이스 commit 
    - jpa는 리플렉션을 사용하기 때문에 기본 생성자가 반드시 필요하다.
    - `hibernate.jdbc.batch_size` 를 통해 버퍼링을 사용할 수 있다.
- flush: 영속성 컨텍스트의 변경내용을 데이터베이스에 반영. context를 `비우는게` 아니다.
    - 변경감지 (dirty checking)
- flush 하는 법
    - em.flush()
    - 트랜잭션 커밋
    - jpql 쿼리 실행
- flush 한다 하여도 1차 캐시가 날아가지는 않는다.
- flush 모드를 설정할 수 있다.
    - FlushModeType.AUTO
    - FlushModeType.COMMIT
    
## 3일차 정리

- `@Entity`가 붙은 클래스는 JPA가 관리한다.
    - `reflection`을 사용하기 때문에 기본 생성자가 필수다.
    - final, enum. interface, inner 클래스 등에는 사용 불가하다.
    - attribute에 final을 사용할 수 없다
- 클래스명과 테이블명을 `snake_case`로 지정해주지만 특정 이름으로 매핑하려면 `@Table`을 사용할 수 있다.
- jpa에선 application loading 시점에 db 테이블을 생성할 수 있는 기능을 제공한다.
    - real 환경에선 사용하지 않는 것이 좋다.
    - 테이블 중심에서 객체 중심으로 
    - 데이터베이스 벤더별 적절한 ddl을 생성해준다.
    - hibernate.hbm2ddl.auto
        - create : 기존 테이블 삭제후 재생성
        - drop : 테이븡을 drop만 한다.
        - create-drop : 종료 시점에 테이블 삭제
        - update : 변경분만 반영, drop은 적용되지 않음
        - validate : entity와 테이블이 정상적으로 매핑됐는지 여부만 확인
        - none : 사용하지 않는다.
- `column`을 이용하면 제약 조건 설정이 가능하다.
- `@Transient`는 테이블과 매핑되지 않고 메모리상에서만 사용된다
- `unique` 제약 조건은 이름이 random하게 작성되기 때문에 잘 사용하지 않는다.
    - 보통은 `@Table` 에서 한다.
- jpa는 기본 키 매핑 전략을 제공한다
- @GeneratedValue는 자동생성 하게 해준다.
    - IDENTITY: 데이터베이스에 위임 (MYSQL)
    - SEQUENCE: 데이터베이스 시퀀스 또는 시리얼 사용(ORACLE)
    - TABLE: 키 생성용 테이블을 별도로 생성
    - AUTO: 벤더별 자동 지정
- `id` 전략에 따라 jpa 작동 방식이 달라질 수 있다.
    - auto_incremnet를 할 경우 `null` 로 설정해야 한다.
    - 만약, id 를 auto_increment등으로 설정할 경우 데이터베이스에 값을 삽입하지 않는다면 id를 알수가 없다. 따라서 `persist()`호출 시 `commit()`을 하지 않고도 insert쿼리를 바로 수행한다.
    
## 4일차 정리

- 객체와 테이블간의 연관관계 정리
    - 방향 : 단방향, 양방향
    - 다중성: 다대일, 일대다, 일대일, 다대다
    - 연관관계의 주인
- 연관관계 전략
    - 객체를 테이블에 맞추어 모델링
-  객체는 참조로, 데이터베이스는 외래키로 연결되어 있다.
- 연관관계는 주인을 결정해야 한다.
    - `mappedBy`를 왜 사용할까?
        - 객체와 테이블간의 연관관계를 맺는 차이를 이해해야 한다.
        - 테이블에서는 `fk`로 모든게 탐색 가능하다. 따라서 방향성 같은게 없다.
        - 객체는 단뱡향이 2개 있는 형태다.
        - 둘 중 하나에서 외래 키를 관리해야 한다.
        - 주인만이 외래 키를 관리하며 반대쪽은 읽기만 가능하다.
        - 주인은 `mappedBy`를 사용하면 안된다. 
        - 외래키가 있는 곳에 주인을 정하자
        - `주인`에 특별한 의미를 부여하지 말자. 비즈니스 로직과는 별 관련이 없다.
    - 객체지향적인 방법인가?
        - flush나 clear를 호출하면 괜찮지만 만약 commit하기 전이라면?
            - 1차 캐시를 기준으로 작업하게된다.
        - 따라서 양뱡한 다 세팅을 해주는게 맞다.
            - 연관관계 편의 메소드를 만들자
                - getter/setter 를 사용할 경우 무한참조가 발생할 수 있다.
                - 어디에 만들어도 상관없다.
                - controller 에서 entity 반환하지 말것
    - 가능한 단방향 매핑을 할 것
        - 양뱡향은 단순히 조회기능이 추가된 것일 뿐

## 5일차 정리

- 연관관계 매핑시 고려사항 
    - 단뱡향 : 연관되어 있는 두 객체중 한곳에서만 참조 가능한 경우
    - 양뱡향 : 연관되어 있는 두 객체가 서로 참조 가능한 경우
        - `mappedby` 사용해야함
- 다대일 : @ManyToOne
    - 단방향
        - 가장 선호하는 방법
        - 선언하고 @ManyToOne 붙이면 끝난
    - 양방향
        - 외래키가 있는쪽이 연관관계 주인다
        - 서로 참조하게 하려면 객체를 추가 또는 set 하는 메서드가 필요하다.
            - 무한루프에 빠질 수 있으니 주의
- 일대다 : @OneToMany
    - 단방향
        - @JoinColumn을 선언해서 사용한다.
            - 선언하지 않을시 외래키를 관리하는 테이블이(join table) 새로 생길 수 있다.
        - 외래키가 다른 테이블에 있기에 insert 외 update 쿼리가 추가적으로 실행될 수 있다.
        - 가능한 사용하지 말 것. **다대일 양뱡향 매핑**을 사용하자
    - 양방향
        - 사실상 존재하지 않는다.
            - mappedBy가 안되기 때문에...
        - `@JoinColumn` 과 `insertable=false updateable=false` 로 흉내낼수 있다.
            - 정상적인 스펙이 아님
- 일대일 : @OneToOne
    - 주 테이블이나 대상 테이블 중에 외래 키 선택 가능
    - 외래키에 유니크 조건이 추가된 관계
    - 단뱡향
        - 어느쪽이든 주 테이블이 될 수 있다.
        - 외래키를 상대 객체가 가지고 있는 단방향은 존재하지 않는다.
        - 외래키에 null이 들어 갈 수 있다.
    - 양방향
        - `mappedBy` 사용해서 양뱡향 관계 구성 가능
        - 대상 테이블에 외래키를 넣는 방법 중 하나
        - 즉시 로딩 된다. (lazy loading 불가)
- 다대다 : @ManyToMany
    - 관계형 데이터베이스에서는 정규화된 테이블 2개롤 다대다 관계를 표현할 수 없다.
        - 객체는 표현 가능하다.
    - 양방향, 단방향 구분시 특별한거 없이 mappedBy로 주관계 선언 가능
    - N:N은 실무에서 가능한 사용하지 말자
        - 단순한 연결만 하는 경우는 없다.
            - created_at 등과 같이 추가적은 column이 생길 수 있다.
        - 중간테이블을 만들어서 `entity`로 승격한다.
            - 중간 테이블과 다른 테이블관의 관계는 `@ManyToOne` 으로 풀어낸다.

## 6일차 정리

- 상속관계 매핑
    - `@MappedSuperclass` 를 이용한다.
    - 관계형 데이터베이스는 상속 관계를 구현할 수 없다.
    - 슈퍼타입과 서브타입 관계라는 모델링 기법이 상속과 유사하여 일부 구현할 수 있다.
        - 각각 테이블을 만들어서 구현 -> join
        - 단일 테이블
            - type으로 구분
                - 서브타입 테이블
                - 객체 입장에서는 사실 똑같이 표현된다.
- 프록시
    - 연관관계가 있는 두 객체가 있을때, 동시에 2개의 객체를 조회하는게 맞을까?
        - 지연로딩 (lazy loading)이란 개념이 존재한다
            - 코드를 호출할때만 쿼리를 가져오는 개념
    - em.find() vs em.getReference()
        - getReference()는 데이터베이스 조회를 미루는 가짜(proxy)
            - 실제 코드가 호출되어 객체가 필요할때 쿼리를 날린다.
        - `obj.getClass()` 를 호출하면 proxy `HiberbateProxy` 의 형태로 보여진다.
    - 프록시 객체는 처음 사용할때 한번만 초기화
    - 프록시 객체는 원본 엔터티를 상속받음. 따라서 타입 체크시 `==` 대신 `instance of` 사용해야한다.
    - 만약, 실제 entity에 대한 `getReference()` 를 호출하면 어떻게될까??
        - proxy가 아닌 entity가 반환된다.
    - 유틸클래스를 이용해 인스턴스 초기화 여부 확인 가능하다.
        - PersistenceUnitUtil.isLoaded()
- 영속성 전이(casecade)
    - 특정 엔티티를 영속상태로 만들때 관계되어 있는 객체도 함께 만들고 싶을때 사용
        - collection 내부 element 또한 영속된다.
    - 영속성 전이와 연관관계는 아무런 상관이 없다.
        - 단순한 편의성을 제공할뿐
    - 종류
        - ALL
        - PERSIST 
        - REMOVE
 - 고아 객체 제거
    - orphanremoval = true
    - 부모 엔터티와 연관관계가 끊어진 자식 엔터티를 자동삭제한다.
