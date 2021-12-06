package me.kalin;

import me.kalin.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("my-jpa");

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();


        try {
            // insert
            Member member = new Member();
            member.setId(null);
            member.setAge(3);
            member.setName("haha");

            entityManager.persist(member);

            // select
            Member firstMember = entityManager.find(Member.class, 1L);
            System.out.println(firstMember);

            System.out.println(firstMember.equals(member));
            System.out.println(firstMember == member);

            // update
            member.setName("hahava");

            // delete
//            entityManager.remove(firstMember);

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }
}
