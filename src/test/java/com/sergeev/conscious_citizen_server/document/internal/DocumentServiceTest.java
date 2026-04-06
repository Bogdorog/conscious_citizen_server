package com.sergeev.conscious_citizen_server.document.internal;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {/*

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private DocumentTemplateService templateService;

    @Mock
    private PdfGeneratorService pdfGenerator;

    @Mock
    private FileStorageService storageService;

    @InjectMocks
    private DocumentService documentService;

    private Incident incident;

    @BeforeEach
    void setUp() {
        IncidentType type = new IncidentType(1L, "PARKING");
        incident = new Incident();
        incident.setId(1L);
        incident.setTitle("Test");
        incident.setMessage("Desc");
        incident.setAddress("Address");
        incident.setType(type);
    }

    @Test
    void shouldGenerateDocumentSuccessfully() {

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(templateService.loadTemplate("incident_type_PARKING"))
                .thenReturn("<h1>{{title}}</h1>");

        when(pdfGenerator.generate(any()))
                .thenReturn("pdf".getBytes());

        when(storageService.saveDocument(any(), any()))
                .thenReturn("path/file.pdf");

        String path = documentService.generateDocument(1L);

        assertThat(path).isEqualTo("path/file.pdf");
        assertThat(incident.getFilePath()).isEqualTo("path/file.pdf");

        verify(incidentRepository).save(incident);
    }

    @Test
    void shouldThrowIfIncidentNotFound() {

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                documentService.generateDocument(1L)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldDownloadDocument() {

        incident.setFilePath("file.pdf");

        when(incidentRepository.findById(1L))
                .thenReturn(Optional.of(incident));

        when(storageService.read("file.pdf"))
                .thenReturn("data".getBytes());

        byte[] result = documentService.download(1L);

        assertThat(result).isNotNull();
    }*/
}
