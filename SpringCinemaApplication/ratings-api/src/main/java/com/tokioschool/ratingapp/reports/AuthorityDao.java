package com.tokioschool.ratingapp.reports;

import com.tokioschool.ratingapp.domains.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityDao extends JpaRepository<Authority,Long> {
}
