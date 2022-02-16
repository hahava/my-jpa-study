package me.kalin.entity.item;

import lombok.Getter;
import lombok.Setter;
import me.kalin.entity.Category;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DiscriminatorColumn(name = "dType")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}
