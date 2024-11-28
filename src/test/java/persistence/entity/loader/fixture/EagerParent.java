package persistence.entity.loader.fixture;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eager_parents")
public class EagerParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private List<Child> children;

    public EagerParent() {

    }

    public EagerParent(String name) {
        this(name, new ArrayList<>());
    }

    private EagerParent(String name, List<Child> children) {
        this.name = name;
        this.children = children;
    }

    public void addChild(Child child) {
        children.add(child);
    }

    public List<Child> getChildren() {
        return children;
    }
}
