package br.com.thomsonreuters.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.thomsonreuters.model.builder.HeroBuilder;
import br.com.thomsonreuters.repository.HeroRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Hero")
@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditingEntityListener.class)
public class Hero implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
    @Id
    private String id;
	
	@Column
    private String name;

    @Column
    private String realName;

    @Column
    private Integer age;

//    @Column
//    private Set<String> powers;
//
//    public Set<String> getPowers() {
//        if (powers == null) {
//            return Collections.emptySet();
//        }
//        return Collections.unmodifiableSet(powers);
//    }

    public static HeroBuilder builder() {
        return new HeroBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hero)) {
            return false;
        }
        Hero hero = (Hero) o;
        return Objects.equals(name, hero.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder("Hero{");
//        sb.append("id='").append(id).append('\'');
//        sb.append(",name='").append(name).append('\'');
//        sb.append(", realName='").append(realName).append('\'');
//        sb.append(", age=").append(age);
////        sb.append(", powers=").append(powers);
//        sb.append('}');
//        return sb.toString();
//    }
}