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

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import br.com.conductor.heimdall.core.enums.Status;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents a ProviderParams registered to the system.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Data
@Table(name = "PROVIDER_PARAMS")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = {"id"})
public class ProviderParam implements Serializable {

    private static final long serialVersionUID = 2575009691412505853L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 180, nullable = false, unique = true)
    private String name;

    @Column(name = "LOCATION", length = 100, nullable = false, unique = true)
    private String location;

    @Column(name = "VALUE", length = 200)
    private String value;

    @Column(name = "CREATION_DATE", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "STATUS", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    private void initValuesPersist() {

        if (Objeto.isBlank(status)) {

            status = Status.ACTIVE;
        }
        creationDate = LocalDateTime.now();
    }
}
