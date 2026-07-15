package com.se_frms.roleAccess.model;

import com.se_frms.access.model.AccessMaster;
import com.se_frms.roleMaster.model.RoleMaster;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name =
                "se_frms_role_access",

        uniqueConstraints = {

                @UniqueConstraint(

                        columnNames = {

                                "role_id",
                                "access_id"

                        }

                )

        }

)

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAccess {

    @Id
    @GeneratedValue(
            strategy =
                    GenerationType.IDENTITY
    )
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name = "role_id"
    )
    private RoleMaster role;

    @ManyToOne
    @JoinColumn(
            name = "access_id"
    )
    private AccessMaster access;

    private Boolean status;

    @Column(name = "created_by")
    private Integer createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;

}
