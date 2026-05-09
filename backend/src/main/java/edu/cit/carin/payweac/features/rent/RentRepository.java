package edu.cit.carin.payweac.features.rent;

import edu.cit.carin.payweac.features.rent.Rent;
import edu.cit.carin.payweac.features.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {
    List<Rent> findByUserOrderByYearDescMonthDesc(User user);
    List<Rent> findByUserAndStatus(User user, Rent.RentStatus status);
}
