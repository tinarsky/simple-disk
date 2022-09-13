package com.github.tinarsky.simpledisk.repos;

import com.github.tinarsky.simpledisk.domain.SystemItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemItemRepo extends JpaRepository<SystemItem, String> {
}
