package com.jmt.wishlist.entities;

import com.jmt.global.entities.BaseEntity;
import com.jmt.member.entities.Member;
import com.jmt.wishlist.constants.WishType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(WishListId.class)
public class WishList extends BaseEntity {

    @Id
    private Long seq; // 등록번호

    @Id
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private WishType wishType;  // 기본키

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member; // 기본키2
    
}
