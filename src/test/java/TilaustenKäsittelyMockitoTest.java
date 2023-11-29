import mockesimerkki.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TilaustenKäsittelyMockitoTest {
    @Mock
    IHinnoittelija hinnoittelijaMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "100.0, 30.0, 20.0",
            "100.0, 30.0, 10.0",
            "150.0, 130.0,42.0",
            "100.0, 30.0, -10.0",
            "100.0, 30.0, -20.0",
            "100.0,100.0, 100.0",
            "100.0, 30.0, -40.0",
            "100.0, 30.0, -50.0",
    })
    public void testaaKäsittelijäWithMockitoHinnoittelija(float alkuSaldo, float listaHinta, float alennus) {
        // Arrange

        float loppuSaldo = alkuSaldo - (listaHinta * (1 - alennus / 100));

        Asiakas asiakas = new Asiakas(alkuSaldo);
        Tuote tuote = new Tuote("TDD in Action", listaHinta);

        // Record
        when(hinnoittelijaMock.getAlennusProsentti(asiakas, tuote))
                .thenReturn(alennus);

        doNothing().when(hinnoittelijaMock).setAlennusProsentti(asiakas, alennus + 5);

        // Act
        TilaustenKäsittely käsittelijä = new TilaustenKäsittely();
        käsittelijä.setHinnoittelija(hinnoittelijaMock);
        käsittelijä.käsittele(new Tilaus(asiakas, tuote));

        // Assert
        assertEquals(loppuSaldo, asiakas.getSaldo(), 0.001);
        verify(hinnoittelijaMock, times(2)).getAlennusProsentti(asiakas, tuote);
        if (tuote.getHinta() >= 100.0) {
            verify(hinnoittelijaMock, times(1)).setAlennusProsentti(asiakas, alennus + 5);
        } else {
            verify(hinnoittelijaMock, times(0)).setAlennusProsentti(any(Asiakas.class), anyFloat());
        }
    }
}
