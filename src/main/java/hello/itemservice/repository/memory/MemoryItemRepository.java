package hello.itemservice.repository.memory;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MemoryItemRepository implements ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>(); //static
    private static long sequence = 0L; //static

    @Override
    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();
        return store.values().stream()
                .filter(item -> {
                    if (ObjectUtils.isEmpty(itemName)) { // 아이템 이름이 빈 칸 일경우 통과
                        return true;
                    }
                    return item.getItemName().contains(itemName); // 아이템 이름이 포함하고 있는 것들만 반환
                }).filter(item -> {
                    if (maxPrice == null) { // 아이템 가격이 빈 칸 일경우 통괴
                        return true;
                    }
                    return item.getPrice() <= maxPrice; // 아이템 가격보다 같거나 작은 것들을 반환
                })
                .collect(Collectors.toList());
    }

    // 테스트 용도로만 사용한다.
    public void clearStore() {
        store.clear();
    }
}
