package com.example.articlesapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.articlesapi.models.Article;

@Repository
public interface ArticleRepository extends JpaRepository <Article, Integer>{
    
}
