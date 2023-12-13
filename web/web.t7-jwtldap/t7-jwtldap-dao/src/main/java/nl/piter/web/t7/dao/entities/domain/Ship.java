/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.dao.entities.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Domain entities are directly exposed through the REST api.
 * No DAO -> Domain translation is done.
 */
@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@Table(name = "ships")
public class Ship {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String shipName;

    private String shipDescription;

    private String referenceId;

    /**
     * Protected Constructor only for bean creation
     */
    protected Ship() {
    }

}
