package com.project.classroom.repository.base;

import com.project.classroom.dto.request.SearchReqDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class SearchRepository<T> {

    private final EntityManager entityManager;

    public SearchRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Transactional
    public List<T> searchByFields(SearchReqDto request, Class<T> entityClass) {

        int limit = request.getLimit();
        int offset = request.getOffset();

        // Đây là danh sách các Map chứa bộ lọc
        List<Map<String, Object>> fieldFiltersList = request.getFilterBy();

        // Đây là danh sách các Map chứa các trường sắp xếp
        List<Map<String, Object>> sortFieldsList = request.getSortBy();

        String entityName = entityClass.getSimpleName();
        StringBuilder queryString = new StringBuilder("SELECT e FROM " + entityName + " e");

        // Nếu fieldFiltersList không rỗng, thêm điều kiện tìm kiếm
        if (!fieldFiltersList.isEmpty()) {
            queryString.append(" WHERE ");
            List<String> conditions = new ArrayList<>();

            // Duyệt qua từng map trong danh sách fieldFiltersList
            for (Map<String, Object> fieldFilters : fieldFiltersList) {
                for (Map.Entry<String, Object> entry : fieldFilters.entrySet()) {
                    String fieldName = entry.getKey();
                    Object fieldValue = entry.getValue();

                    // Kiểm tra xem trường có phải là kiểu String không
                    if (fieldValue instanceof String) {
                        // Sử dụng ILIKE cho các trường kiểu String
                        conditions.add("LOWER(e." + fieldName + ") LIKE LOWER(:" + fieldName + ")");
                    } else {
                        // Sử dụng = cho các kiểu dữ liệu khác
                        conditions.add("e." + fieldName + " = :" + fieldName);
                    }
                }
            }
            queryString.append(String.join(" AND ", conditions));
        }

        // Thêm phần sắp xếp nếu sortFieldsList không rỗng
        if (sortFieldsList != null && !sortFieldsList.isEmpty()) {
            List<String> orderClauses = new ArrayList<>();

            // Duyệt qua từng map trong danh sách sortFieldsList
            for (Map<String, Object> sortFields : sortFieldsList) {
                for (Map.Entry<String, Object> sortField : sortFields.entrySet()) {
                    String fieldName = sortField.getKey();
                    String orderDirection = sortField.getValue().equals("asc") ? "ASC" : "DESC";
                    orderClauses.add("e." + fieldName + " " + orderDirection);
                }
            }
            queryString.append(" ORDER BY ").append(String.join(", ", orderClauses));
        }

        // Tạo truy vấn
        TypedQuery<T> query = entityManager.createQuery(queryString.toString(), entityClass);

        // Thiết lập tham số cho các bộ lọc
        for (Map<String, Object> fieldFilters : fieldFiltersList) {
            for (Map.Entry<String, Object> entry : fieldFilters.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

                // Thiết lập giá trị cho tham số
                if (fieldValue instanceof String) {
                    // Bao quanh giá trị với % cho LIKE
                    query.setParameter(fieldName, "%" + fieldValue.toString().toLowerCase() + "%");
                } else {
                    query.setParameter(fieldName, fieldValue);
                }
            }
        }

        // Set limit và offset cho phân trang
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        // Thực thi truy vấn và trả về kết quả
        return query.getResultList();
    }



}
