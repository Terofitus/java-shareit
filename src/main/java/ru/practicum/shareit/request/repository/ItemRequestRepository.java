package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends CrudRepository<ItemRequest, Integer>,
        PagingAndSortingRepository<ItemRequest, Integer> {
    List<ItemRequest> getAllByRequestorId(Integer requestorId);

    @Query("select ir from ItemRequest as ir where ir.requestor.id <> ?1")
    List<ItemRequest> findAllWithoutOwnerRequests(Integer ownerId, Pageable pageable);
}
