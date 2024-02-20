package com.example.articlesapi.controllers;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.articlesapi.dto.ArticleDTO;
import com.example.articlesapi.models.Article;
import com.example.articlesapi.models.User;
import com.example.articlesapi.services.ArticleService;
import com.example.articlesapi.services.UserService;

@RestController
@RequestMapping("/articles")
public class ArticlesController {
    private final ArticleService articleService;
    private final UserService userService;

    public ArticlesController(ArticleService articleService, UserService userService){
        this.articleService = articleService;
        this.userService = userService;
    }

    @PostMapping()
    public String post(@RequestBody Article article, Authentication authentication){
        articleService.save(article);
        String username = authentication.getName();
        User user = userService.getUser(username).get();
        article.setOwner(user);
        articleService.save(article);
        return "article has been saved";
    }

    // @GetMapping()
    // public List<Article> getAll(){
    //     return articleService.findAll();
    // }

    @GetMapping()
    public List<ArticleDTO> getAll(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size
                                ){
        Pageable pageable = PageRequest.of(page, size);
        List <Article> articles = articleService.findAll(pageable).getContent();
        List <ArticleDTO> dtos = new ArrayList<>();
        for (Article article : articles){
            ArticleDTO dto = new ArticleDTO();
            dto.setId(article.getId());
            dto.setArticle(article.getArticle());
            dto.setOwnerId(article.getOwner().getId());
            dtos.add(dto);
        }
        return dtos;
    }

    @GetMapping("/{id}")
    public ArticleDTO getOne(@PathVariable int id) throws NoSuchElementException{
        Article article = articleService.findById(id);
        if (article == null) {
            throw new NoSuchElementException("no article with specified id found");
        }
        ArticleDTO articleDTO = new ArticleDTO(article.getId(), article.getArticle(), article.getOwner().getId());
        return articleDTO;
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable int id, Authentication authentication){
        Article article = articleService.findById(id);
        if (article == null) {
            return HttpStatus.NOT_FOUND;
        }
        String username = authentication.getName();
        User user = userService.getUser(username).get();
        if (article.getOwner().getId() != user.getId()){
            return HttpStatus.FORBIDDEN;
        }
        articleService.delete(id);
        return HttpStatus.ACCEPTED;
    }

    @ExceptionHandler
    public String noArticlefound(NoSuchElementException e){
        return e.getMessage();
    }

    @PatchMapping("/{id}")
    public HttpStatus update(@PathVariable int id, @RequestBody Article newArticle, Authentication authentication){
        Article article = articleService.findById(id);
        if (article == null) {
            return HttpStatus.NOT_FOUND;
        }
        String username = authentication.getName();
        User user = userService.getUser(username).get();
        System.out.println(article.getOwner().getId());
        System.out.println(user.getId());
        if (article.getOwner().getId() != user.getId()){
            return HttpStatus.FORBIDDEN;
        }
        article.setArticle(newArticle.getArticle());
        articleService.save(article);
        return HttpStatus.ACCEPTED;
    }
}
