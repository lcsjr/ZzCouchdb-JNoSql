package br.com.thomsonreuters.model.builder;

import br.com.thomsonreuters.model.Hero;

public class HeroBuilder {


	private String id;
	
	private String name;

    private String realName;

    private Integer age;

//    private Set<String> powers = Collections.emptySet();


    public HeroBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public HeroBuilder withRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public HeroBuilder withAge(Integer age) {
        this.age = age;
        return this;
    }

//    public HeroBuilder withPowers(Set<String> powers) {
//        this.powers = powers;
//        return this;
//    }

    public Hero build() {
        return new Hero(id, name, realName, age);
    }
}