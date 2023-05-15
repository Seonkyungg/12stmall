package stmall.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import stmall.ProductApplication;
import stmall.domain.StockDecreased;
import stmall.domain.StockIncreased;

@Entity
@Table(name = "Inventory_table")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productName;

    private int stock;

    @PostUpdate
    public void onPostUpdate() {

    }

    public static InventoryRepository repository() {
        InventoryRepository inventoryRepository = ProductApplication.applicationContext.getBean(
            InventoryRepository.class
        );
        return inventoryRepository;
    }

    public static void decreasedStock(DeliveryCompleted deliveryCompleted) {
        
        repository().findById(deliveryCompleted.getProductId()).ifPresent(inventory->{
            inventory.setStock(inventory.getStock()-deliveryCompleted.getQty()); 
            repository().save(inventory);

            StockDecreased stockDecreased = new StockDecreased(inventory);
            stockDecreased.publishAfterCommit();
         });

    }

    public static void increasedStock(DeliveryReturned deliveryReturned) {
        repository().findById(deliveryReturned.getProductId()).ifPresent(inventory->{
            
            inventory.setStock(inventory.getStock()+deliveryReturned.getQty()); 
            repository().save(inventory);

            StockIncreased stockIncreased = new StockIncreased(inventory);
            stockIncreased.publishAfterCommit();
         });
    }
}
