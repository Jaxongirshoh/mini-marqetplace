package dev.mustafa.mini_marqetplace.service;

import dev.mustafa.mini_marqetplace.exception.NotFoundException;
import dev.mustafa.mini_marqetplace.model.dto.PageResponse;
import dev.mustafa.mini_marqetplace.model.dto.ProductDto;
import dev.mustafa.mini_marqetplace.model.dto.ProductResponseDto;
import dev.mustafa.mini_marqetplace.model.entity.Product;
import dev.mustafa.mini_marqetplace.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void create(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.name());
        product.setPrice(productDto.price());
        product.setStockQuantity(productDto.stockQuantity());

        productRepository.save(product);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void update(ProductDto productDto, Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("product with %s id not found".formatted(id)));

        if (productDto.name() != null) {
            product.setName(productDto.name());
        }

        if (productDto.price() != 0) {
            product.setPrice(productDto.price());
        }

        if (productDto.stockQuantity() != 0) {
            product.setStockQuantity(productDto.stockQuantity());
        }

        productRepository.update(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponseDto getById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("product with %s id not found".formatted(id)));
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }

    @Transactional
    public PageResponse<ProductResponseDto> getAsPage(Pageable pageable) {

        List<ProductResponseDto> products = productRepository.getAsPage(pageable.getPageSize(),pageable.getOffset())
                .stream()
                .map(product -> new ProductResponseDto(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStockQuantity()
                        )
                ).toList();

        Long count = productRepository.getProductCount()
                .orElse(0L);

        return PageResponse.of(products,pageable.getPageNumber(),
                pageable.getPageSize(),
                count);
    }
}
