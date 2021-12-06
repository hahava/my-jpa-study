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
    