package com.business.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document
@NoArgsConstructor
public class CatalogItem {
  
  @Id
  public String id;

  private String name;
  private String description;
  private List<String> materialsInfo = new ArrayList<>();
  private String careInstructions;
  private BigDecimal price;
  private String imageUrl;
  private String imageAltDescription;
  private CategoryType categoryType;
  private String productExternalId;
  private List<String> searchTerms = new ArrayList<>();
  private List<Option> options = new ArrayList<>();
  
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Option {
    
    private String color;
    private String styleClassName;
    private String artworkThumbnailUrl;
    private String artworkPreviewUrl;
    private String frontTeeThumbnailUrl;
    private String frontTeePreviewUrl;
    private String backTeeThumbnailUrl;
    private String backTeePreviewUrl;
    private List<Size> sizes = new ArrayList<>();
  }
  
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Size {
    
    private String size;
    private String externalVariantId;
  }
}
