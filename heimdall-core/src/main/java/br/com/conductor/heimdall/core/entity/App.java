
package br.com.conductor.heimdall.core.entity;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================LICENSE_END===================================
 */

import br.com.conductor.heimdall.core.enums.Status;
import br.com.twsoftware.alfred.object.Objeto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents a App registered to the system.
 *
 * @author Filipe Germano
 */
@Data
@Table(name = "APPS")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class App implements Serializable {

    private static final long serialVersionUID = -8080005929936415705L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CLIENT_ID", length = 250, nullable = false, unique = true)
    private String clientId;

    @Column(name = "NAME", length = 180, nullable = false, unique = true)
    private String name;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @ManyToOne
    @JoinColumn(name = "DEVELOPER_ID", nullable = false)
    private Developer developer;

    @Column(name = "CREATION_DATE", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "STATUS", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "APP_ID", insertable = false, updatable = false)
    @JsonIgnoreProperties({"app", "plans"})
    private List<AccessToken> accessTokens;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "APPS_PLANS",
            joinColumns = @JoinColumn(name = "APP_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PLAN_ID", referencedColumnName = "ID"))
    @JsonIgnoreProperties({"api"})
    private List<Plan> plans;

    @JsonIgnore
    @Column(name = "TAGS", length = 2000)
    private String tag;

    @Transient
    private List<String> tags;

    @PrePersist
    private void initValuesPersist() {

        if (Objeto.isBlank(status)) {

            status = Status.ACTIVE;
        }

        creationDate = LocalDateTime.now();
    }

    @PostLoad
    private void loadMethods() {

        if (Objeto.notBlank(tag)) {

            tags = Lists.newArrayList(tag.split(";"));
//               tags = Arrays.asList(tag.split(";"));
        }
    }

}
