package com.ssafy.staticsserver.news.entitiy;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "foreign_news")
public class ForeignNewsMongo {
    @Id
    private String id;
    private String title;
    private String description;
    private String url;
    @Field(name = "published_at")
    private LocalDateTime published_at;
    @Field(name = "image_url")
    private String image_url;
    private List<String> categories;
    private String country;
    private List<String> keywords;
    private Integer sentiment;
    private String rawDataRef;

    @Override
    public String toString() {
        return "ForeignNewsMongo{" +
            "id='" + id + '\'' +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", url='" + url + '\'' +
            ", published_at=" + published_at +
            ", image_url='" + image_url + '\'' +
            ", categories=" + categories +
            ", country='" + country + '\'' +
            ", keywords=" + keywords +
            ", sentiment=" + sentiment +
            ", rawDataRef='" + rawDataRef + '\'' +
            '}';
    }
}
