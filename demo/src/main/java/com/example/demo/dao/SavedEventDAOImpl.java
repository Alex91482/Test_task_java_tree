package com.example.demo.dao;


import com.example.demo.entity.SavedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SavedEventDAOImpl implements SavedEventDAO{

    private SavedEventRepository savedEventRepository;

    @Autowired
    public SavedEventDAOImpl(SavedEventRepository savedEventRepository){
        this.savedEventRepository = savedEventRepository;
    }

    public void save(SavedEvent savedEvent){
        savedEventRepository.save(savedEvent).subscribe();
    }

    public Mono<SavedEvent> findById(Long id){
        return savedEventRepository.findById(id);
    }

    public Flux<SavedEvent> findAll(){
        return savedEventRepository.findAll();
    }

    public Flux<SavedEvent> findByOccurredName(String name){
        return (name != null) ? savedEventRepository.findByOccurredEvent(name) : savedEventRepository.findAll();
    }

    public Mono<SavedEvent> update(SavedEvent savedEvent){
        return savedEventRepository.save(savedEvent);
    }

    public Mono<Void> delete(Long id){
        return savedEventRepository.deleteById(id);
    }

    public Mono<SavedEvent> getTheLatestEntry(){
        return savedEventRepository.findFirstByOrderByEventTimeDesc();
    }
}
