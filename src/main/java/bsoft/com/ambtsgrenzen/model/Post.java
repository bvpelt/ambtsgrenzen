package bsoft.com.ambtsgrenzen.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Post {
    private Integer id;
    private Integer userId;
    private String title;
    private String body;
}
