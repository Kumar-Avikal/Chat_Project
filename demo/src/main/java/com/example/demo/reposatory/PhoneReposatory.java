package com.example.demo.reposatory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PhoneNumber;

@Repository
public interface PhoneReposatory extends JpaRepository<PhoneNumber, Long> {

}
