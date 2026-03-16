package com.Project.QuizApp.DAO;

import com.Project.QuizApp.Model.QuizClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizDao extends JpaRepository<QuizClass , Integer> {

}
