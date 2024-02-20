package com.example.articlesapi.services;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.articlesapi.models.Article;
import com.example.articlesapi.repositories.ArticleRepository;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }
    

    public List <Article> findAll(){
        return articleRepository.findAll();
    }

    public Page<Article> findAll(Pageable pageable){
        return articleRepository.findAll(pageable);
    }

    public Article findById(int id){
        return articleRepository.findById(id).get();
    }

    public void save(Article article){
        articleRepository.save(article);
    }

    public void delete(int id){
        articleRepository.delete(findById(id));
    }
}
