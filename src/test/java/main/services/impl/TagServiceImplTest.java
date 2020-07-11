package main.services.impl;

import main.MainTest;
import main.model.entities.Tag;
import main.repositories.TagRepository;
import main.services.TagService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainTest.class)
public class TagServiceImplTest {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    private Tag existingTag;
    private Tag newTag;
    private Tag search1;
    private Tag search2;
    private Tag tearch3;

    //==================================================================================================================

    @Before
    public void setUp() throws Exception {
        existingTag = new Tag();
        existingTag.setName("tag20");

        newTag = new Tag();
        newTag.setName("newTag1");

        search1 = new Tag();
        search1.setName("search1");
        search2 = new Tag();
        search2.setName("search2");
        tearch3 = new Tag();
        tearch3.setName("tearch3");
        
        tagService.createTagIfNoExistsAndReturn(search1.getName());
        tagService.createTagIfNoExistsAndReturn(search2.getName());
        tagService.createTagIfNoExistsAndReturn(tearch3.getName());
    }

    @After
    public void tearDown() throws Exception {
        tagService.removeByTagName(newTag.getName());
        tagService.removeByTagName(search1.getName());
        tagService.removeByTagName(search2.getName());
        tagService.removeByTagName(tearch3.getName());
    }

    @Test
    public void tryCreateNonexistentTag() {
        Tag tag = tagService.createTagIfNoExistsAndReturn(newTag.getName());
        tagService.removeByTagName(newTag.getName());
        assertNotNull(tag);
    }

    @Test
    public void tryCreateExistingTag() {
        tagService.createTagIfNoExistsAndReturn(existingTag.getName());
        int actualSize = tagService.findAllTagsByQuery(existingTag.getName()).size();
        assertEquals(1, actualSize);
    }

    @Test
    public void findByName() {
        System.err.println(existingTag);
        Tag actual = tagService.findByName(existingTag.getName());
        assertNotNull(actual);
        assertEquals(existingTag.getName(), actual.getName());
    }

    @Test
    public void removeByTagName() {
        Tag removedTag = new Tag();
        removedTag.setName("removedTag");

        if (tagService.findByName(removedTag.getName()) == null) {
            tagRepository.saveAndFlush(removedTag);
        }

        tagService.removeByTagName(removedTag.getName());
        Tag actual = tagService.findByName(removedTag.getName());
        assertNull(actual);
    }

    @Test
    public void findAllTagsByQuery() {
        List<Tag> expected = new ArrayList<>();
        expected.add(search1);
        expected.add(search2);

        List<Tag> actual = tagService.findAllTagsByQuery("sea");
        actual.sort(Comparator.comparing(Tag::getName));
        assertEquals("Size: " + 2, "Size: " + actual.size());
        assertEquals(expected.get(0), actual.get(0));
        assertEquals(expected.get(1), actual.get(1));
    }
}