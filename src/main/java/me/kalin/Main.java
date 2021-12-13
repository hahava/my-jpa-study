package me.kalin;

import me.kalin.entity.Member;
import me.kalin.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.lang.reflect.Proxy;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("my-jpa");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        try {
            Team team = new Team();
            team.setName("firstTeam");
            // team.getMembers().add() // 이런건 안됨...
            entityManager.persist(team);

            Member member = new Member();
            member.setUserName("firstMember");
            member.setTeam(team);
            // member.setTeamId(team.getId()); // 객체지향 관점으로는 매우 이상함 ...
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            Member findMember = entityManager.find(Member.class, member.getId());
            List<Member> members = findMember.getTeam().getMembers();

            members.stream().forEach(m -> System.out.println(m.getUserName()));

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }
}
