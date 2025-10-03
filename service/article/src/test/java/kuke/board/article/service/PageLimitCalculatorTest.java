package kuke.board.article.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageLimitCalculatorTest {

    @Test
    void calculatePageLimitTest() {
        calculatorPageLimitTest(1L, 30L, 10L, 301L);
        calculatorPageLimitTest(7L, 30L, 10L, 301L);
        calculatorPageLimitTest(10L, 30L, 10L, 301L);
        calculatorPageLimitTest(11L, 30L, 10L, 601L);
        calculatorPageLimitTest(12L, 30L, 10L, 601L);
    }

    void calculatorPageLimitTest(Long page, Long pageSize, Long movablePageCount, Long expected) {
        Long actual = PageLimitCalculator.calculatorPageLimit(page, pageSize, movablePageCount);
        assertEquals(expected, actual);
    }
}