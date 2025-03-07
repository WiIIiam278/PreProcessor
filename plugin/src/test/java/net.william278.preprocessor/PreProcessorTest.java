/*
 * This file is part of WiIIiam278/PreProcessor, licensed under CC BY-NC-SA 4.0 (the "License").
 * The License applies under the Adapted Material clause of CC BY-NC-SA 4.0 (see Section 1 - Definitions)
 * WiIIiam278/PreProcessor is a derivative work of ToCraft/PreProcessor (https://github.com/ToCraft/PreProcessor)
 *
 *  Copyright (c) To_Craft <development@tocraft.dev>
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 * You can obtain a copy of the license at: https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package net.william278.preprocessor;

import net.william278.preprocessor.util.ParseException;
import net.william278.preprocessor.util.PreProcessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A simple unit test for the 'org.example.greeting' plugin.
 */
class PreProcessorTest {
    private static final Map<String, Object> vars = new HashMap<String, Object>() {
        {
            put("zero", "0");
            put("one", "1");
            put("two", "2");
            put("test", "hello");
        }
    };

    private static final PreProcessor preProcessor = new PreProcessor(vars);

    @Test
    void testEvalExpression() {
        // existence of vars
        assertFalse(preProcessor.evalExpression("zero"));
        assertTrue(preProcessor.evalExpression("one"));
        assertTrue(preProcessor.evalExpression("test"));
        assertFalse(preProcessor.evalExpression("invalid"));
        // a == b
        assertFalse(preProcessor.evalExpression("one == 0"));
        assertTrue(preProcessor.evalExpression("one == 1"));
        assertFalse(preProcessor.evalExpression("1 == zero"));
        assertTrue(preProcessor.evalExpression("1 == one"));
        // a != b
        assertFalse(preProcessor.evalExpression("zero != 0"));
        assertTrue(preProcessor.evalExpression("zero != 1"));
        assertTrue(preProcessor.evalExpression("zero != one"));
        assertFalse(preProcessor.evalExpression("one != 1"));
        // a > b
        assertTrue(preProcessor.evalExpression("one > 0"));
        assertFalse(preProcessor.evalExpression("one > 1"));
        assertFalse(preProcessor.evalExpression("one > 2"));
        assertTrue(preProcessor.evalExpression("1 > zero"));
        assertFalse(preProcessor.evalExpression("1 > one"));
        assertFalse(preProcessor.evalExpression("1 > two"));
        // a >= b
        assertTrue(preProcessor.evalExpression("one >= 0"));
        assertTrue(preProcessor.evalExpression("one >= 1"));
        assertFalse(preProcessor.evalExpression("one >= 2"));
        assertTrue(preProcessor.evalExpression("1 >= zero"));
        assertTrue(preProcessor.evalExpression("1 >= one"));
        assertFalse(preProcessor.evalExpression("1 >= two"));
        // a < b
        assertFalse(preProcessor.evalExpression("one < 0"));
        assertFalse(preProcessor.evalExpression("one < 1"));
        assertTrue(preProcessor.evalExpression("one < 2"));
        assertFalse(preProcessor.evalExpression("1 < zero"));
        assertFalse(preProcessor.evalExpression("1 < one"));
        assertTrue(preProcessor.evalExpression("1 < two"));
        // a <= b
        assertFalse(preProcessor.evalExpression("one <= 0"));
        assertTrue(preProcessor.evalExpression("one <= 1"));
        assertTrue(preProcessor.evalExpression("one <= 2"));
        assertFalse(preProcessor.evalExpression("1 <= zero"));
        assertTrue(preProcessor.evalExpression("1 <= one"));
        assertTrue(preProcessor.evalExpression("1 <= two"));
        // a && b
        assertTrue(preProcessor.evalExpression("one && one"));
        assertFalse(preProcessor.evalExpression("one && zero"));
        assertFalse(preProcessor.evalExpression("zero && one"));
        assertFalse(preProcessor.evalExpression("zero && zero"));
        // a || b
        assertTrue(preProcessor.evalExpression("one || one"));
        assertTrue(preProcessor.evalExpression("one || zero"));
        assertTrue(preProcessor.evalExpression("zero || one"));
        assertFalse(preProcessor.evalExpression("zero || zero"));
        // ||and && nested
        assertTrue(preProcessor.evalExpression("one && one || one"));
        assertTrue(preProcessor.evalExpression("one && zero || one"));
        assertFalse(preProcessor.evalExpression("one && zero || one && zero"));
        assertTrue(preProcessor.evalExpression("one || zero && one || zero"));
        assertFalse(preProcessor.evalExpression("zero || zero && one || zero"));
    }

    @Test
    void testConvertSource() {
        // unexpected endif
        assertThrows(ParseException.class, () -> preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#endif");
            }
        }));
        // unexpected else
        assertThrows(ParseException.class, () -> preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#else");
            }
        }));
        // unexpected elseif
        assertThrows(ParseException.class, () -> preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#elseif");
            }
        }));
        // elseif after else
        assertThrows(ParseException.class, () -> preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//#else");
                add("//#elseif one");
                add("//#endif");
            }
        }));
        // missing endif
        assertThrows(ParseException.class, () -> preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
            }
        }));
        assertThrows(ParseException.class, () -> preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//#if one");
                add("//#if one");
                add("//#endif");
            }
        }));
        // missing space
        assertThrows(ParseException.class, () -> preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#ifone");
                add("//#endif");
            }
        }));
        assertThrows(ParseException.class, () -> preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//#elseiftwo");
                add("//#endif");
            }
        }));
        // empty if condition
        assertThrows(ParseException.class, () -> preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if");
                add("//#endif");
            }
        }));

        // test with add("code");
        // if one ... endif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//$$ code");
                add("//#endif");
            }
        }));
        // if zero ... endif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("code");
                add("//#endif");
            }
        }));
        // if one ... else ... endif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//$$ code");
                add("//#else");
                add("code");
                add("//#endif");
            }
        }));
        // if zero ... else ... endif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("code");
                add("//#endif");
            }
        }));
        // if one ... elseif zero ... endif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//$$ code");
                add("//#elseif zero");
                add("code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif zero");
                add("code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//$$ code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#endif");
            }
        }));
        // if zero ... elseif one ... endif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif one");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif one");
                add("code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif one");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("code");
                add("//#elseif one");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif one");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif one");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif one");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("code");
                add("//#elseif one");
                add("code");
                add("//#endif");
            }
        }));
        // if one ... elseif one ... endif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif one");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif one");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif one");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//$$ code");
                add("//#elseif one");
                add("code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif one");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//$$ code");
                add("//#elseif one");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif one");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif one");
                add("code");
                add("//#endif");
            }
        }));
        // if ... elseif ... else ... endif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif one");
                add("code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif one");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("code");
                add("//#elseif one");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//$$ code");
                add("//#elseif one");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#else");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }));
        // multiple elseif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#else");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#elseif zero");
                add("code");
                add("//#elseif zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }));
        // nested if
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
                add("//#else");
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("code");
                add("//#endif");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
                add("//#else");
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
                add("//#endif");
            }
        }));
        // nested elseif
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
                add("//#elseif one");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
                add("//#elseif one");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif one");
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("code");
                add("//#endif");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("code");
                add("//#elseif one");
                add("//#if zero");
                add("code");
                add("//#else");
                add("code");
                add("//#endif");
                add("//#else");
                add("code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
                add("//#elseif zero");
                add("//$$ code");
                add("//#else");
                add("code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("//#elseif zero");
                add("//#if zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
                add("//#elseif zero");
                add("//$$ code");
                add("//#else");
                add("//$$ code");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("//#elseif one");
                add("//#if one");
                add("//$$ code");
                add("//#else");
                add("//#endif");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//#elseif one");
                add("//#if one");
                add("code");
                add("//#else");
                add("//#endif");
                add("//#endif");
            }
        }));
        assertEquals(new ArrayList<String>() {
            {
                add("//#if one");
                add("//#elseif one");
                add("//#if one");
                add("//#endif");
                add("//$$ code");
                add("//#endif");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if one");
                add("//#elseif one");
                add("//#if one");
                add("//#endif");
                add("code");
                add("//#endif");
            }
        }));
    }

    @Test
    void testRemoveComments() {
        PreProcessor preProcessor = new PreProcessor(true, vars);

        assertEquals(new ArrayList<String>() {
            {
                add("code");
                add("code");
            }
        }, preProcessor.convertSource(new ArrayList<String>() {
            {
                add("//#if zero");
                add("//$$ code");
                add("code");
                add("//#elseif zero");
                add("//$$ code");
                add("code");
                add("//#else");
                add("//$$ code");
                add("code");
                add("//#endif");
            }
        }));
    }
}
