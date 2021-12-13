package me.kalin.entity;

import javax.persistence.*;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

//    @Column(name = "team_id")
//    private Long teamId;

   @ManyToOne
   @JoinColumn(name = "team_id")
    private Team team;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
        team.getMembers().add(this); // 연관관계 매핑이됨
    }

    //    public Long getTeamId() {
//        return teamId;
//    }

//    public void setTeamId(Long teamId) {
//        this.teamId = teamId;
//    }
}
