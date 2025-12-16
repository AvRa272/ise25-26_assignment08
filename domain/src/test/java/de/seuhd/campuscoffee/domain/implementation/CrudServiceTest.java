package de.seuhd.campuscoffee.domain.implementation;

import static org.assertj.core.api.Assertions.assertThat;

import de.seuhd.campuscoffee.domain.exceptions.DuplicationException;
import de.seuhd.campuscoffee.domain.model.objects.Pos;
import de.seuhd.campuscoffee.domain.ports.data.CrudDataService;
import de.seuhd.campuscoffee.domain.ports.data.PosDataService;
import de.seuhd.campuscoffee.domain.tests.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// test the crud service with POS data
@ExtendWith(MockitoExtension.class)
public class CrudServiceTest {
    @Mock
    private PosDataService posDataService;

    @Mock
    private CrudDataService<Pos, Long> crudDataService;

    private CrudServiceImpl<Pos, Long> crudService;


    // take care of the dependencies of CrudServiceImpl class
    @BeforeEach
    void beforeEach() {
        crudService = new CrudServiceImpl<Pos, Long>(Pos.class) {
            @Override
            protected CrudDataService<Pos, Long> dataService() {
                return posDataService;
            }
        };
    }

    @Test
    void clearAllPosRecords() {
        // "when" can only be used when the return type of a method under mocking is not void
        // when the return type is none, we can either totally ignore it or use doNothing().when(...).clear()
        doNothing().when(posDataService).clear();

        // when
        crudService.clear();
        // then
        verify(posDataService).clear();
    }


    @Test
    void Delete() {
        Pos pos = TestFixtures.getPosFixtures().getFirst();
        assertNotNull(pos.id());

        // do nothing when the delete is called!
        //doNothing().when(posDataService).getById(pos.id());

        // when
        crudService.delete(pos.id());
        // then
        verify(posDataService).delete(pos.id());
    }

    @Test
    void UnsuccessfulUpsert() {
        Pos pos = TestFixtures.getPosFixtures().getFirst();
        Pos toChange = pos.toBuilder()
                .description("fine Waffles")
                .build();

        DuplicationException e = new DuplicationException(Pos.class, "Duplication exception", "Pos already exists");

        when(posDataService.getById(toChange.id())).thenReturn(toChange);
        doThrow(e).when(posDataService).upsert(toChange);

        // when, then
        assertThrows(DuplicationException.class, () -> crudService.upsert(toChange));

        verify(posDataService).upsert(toChange);

    }

    // unneeded Tests
//    @Test
//    void GetAllRecords() {
//        List<Pos> posList = TestFixtures.getPosFixtures().stream().toList();
//        when(posDataService.getAll()).thenReturn(posList);
//
//        // when
//        List<Pos> retrievedPosList = crudService.getAll();
//
//        // then
//        assertEquals(retrievedPosList.size(), posList.size());
//        assertEquals(retrievedPosList, posList);
//
//        verify(posDataService).getAll();
//    }
//
//    @Test
//    void GetById() {
//        Pos pos = TestFixtures.getPosFixtures().getFirst();
//        assertNotNull(pos);
//        assertNotNull(pos.id());
//
//        when(posDataService.getById(pos.id())).thenReturn(pos);
//
//        // when
//        Pos retrievedPos = crudService.getById(pos.id());
//        // then
//        assertEquals(retrievedPos.id(), pos.id());
//
//        verify(posDataService).getById(pos.id());
//    }
//
//
//    @Test
//    void SuccessfulUpsert () {
//        Pos pos = TestFixtures.getPosFixtures().getFirst();
//        Pos toChange = pos.toBuilder()
//                        .description("fine Waffles")
//                                .build();
//
//        when(posDataService.upsert(toChange)).thenReturn(toChange);
//
//        // when
//        Pos changedPos = crudService.upsert(toChange);
//
//        // then
//        assertEquals(toChange, changedPos);
//
//        verify(posDataService).upsert(toChange);
//
//    }

}