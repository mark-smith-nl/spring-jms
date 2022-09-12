package nl.smith.springmdb.domain;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString()
@XmlRootElement(name = "OrderTransaction")
public class OrderTransaction {

    private Person from;

    private Person to;

    private BigDecimal amount;

    public OrderTransaction(final Person from, final Person to, final BigDecimal amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}
