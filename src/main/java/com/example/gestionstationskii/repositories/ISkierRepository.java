package com.example.gestionstationskii.repositories;

import com.example.gestionstationskii.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISkierRepository extends JpaRepository<Skier, Long> {
   List<Skier> findBySubscription_TypeSub(TypeSubscription typeSubscription);
   Skier findBySubscription(Subscription subscription);


   @Query("select s from Skier s JOIN s.pistes p where  p.color=:color ")
   List<Skier> skiersByColorPiste(@Param("color") Color color);

}


