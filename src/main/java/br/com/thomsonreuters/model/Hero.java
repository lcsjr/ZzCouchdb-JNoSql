package br.com.thomsonreuters.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    @GeneratedValue(strategy= GenerationType.IDENTITY)
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