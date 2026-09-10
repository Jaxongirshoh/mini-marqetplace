package dev.mustafa.mini_marqetplace.controller;

import dev.mustafa.mini_marqetplace.model.dto.BaseResponse;
import dev.mustafa.mini_marqetplace.model.dto.PageResponse;
import dev.mustafa.mini_marqetplace.model.dto.ProductDto;
import dev.mustafa.mini_marqetplace.model.dto.ProductResponseDto;
import dev.mustafa.mini_marqetplace.service.ProductService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<String>> create(
            @RequestBody ProductDto productDto
    ) {
        productService.create(productDto);
        return ResponseEntity.ok(new BaseResponse<>("created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<String>> update(
        @RequestBody ProductDto productDto,
        @PathVariable Integer id
    ){
        productService.update(productDto,id);
        return ResponseEntity.ok(new BaseResponse<>("updated"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductResponseDto>> getById(
            @PathVariable Integer id
    ){
        ProductResponseDto response = productService.getById(id);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<ProductResponseDto>>> getAsPage(
            Pageable pageable
    ){
        PageResponse<ProductResponseDto> pagedModel = productService.getAsPage(pageable);
        return ResponseEntity.ok(new BaseResponse<>(pagedModel));
    }
}
