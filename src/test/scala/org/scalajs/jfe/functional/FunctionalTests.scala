package org.scalajs.jfe.functional

import org.scalajs.jfe.ASTUtils
import org.scalajs.jfe.util.TextUtils
import org.scalatest.BeforeAndAfter
import org.scalatest.funspec.AnyFunSpec

case class NumericTestType(name: String, max: AnyVal)

class FunctionalTests extends AnyFunSpec with BeforeAndAfter {

  import org.scalajs.jfe.TestUtils._

  val NumericTestTypes = Set(
    NumericTestType("byte", Byte.MaxValue),
    NumericTestType("Byte", Byte.MaxValue),
    NumericTestType("char", Char.MaxValue),
    NumericTestType("Character", Char.MaxValue),
    NumericTestType("short", Short.MaxValue),
    NumericTestType("Short", Short.MaxValue),
    NumericTestType("int", Int.MaxValue),
    NumericTestType("Integer", Int.MaxValue),
    NumericTestType("long", Long.MaxValue),
    NumericTestType("Long", Long.MaxValue),
    NumericTestType("float", Float.MaxValue),
    NumericTestType("Float", Float.MaxValue),
    NumericTestType("double", Double.MaxValue),
    NumericTestType("Double", Double.MaxValue),
  )

  before {
    TextUtils.clearFreshNames()
  }

  /*it("links Java programs and prints to the console") {
    val src =
      """class Main {
        |    public static void main() {
        |        System.out.println("The linker works");
        |    }
        |}
        |""".stripMargin
    assertRun(src, "The linker works")
  }*/

  describe("Javalib:") {
    it("calls JDK static methods") {
      val src =
        """class Main {
          |    public static void main() {
          |        System.out.println(Integer.toHexString(200));
          |    }
          |}""".stripMargin
      assertRun(src, "c8")
    }
  }

  describe("Type features:") {
    it("boxes assignments") {
      val src =
        """class Main {
          |    public static void main() {
          |        Boolean bool = true;
          |        Byte b = 10;
          |        Short s = 11;
          |        Integer i = 12;
          |        Long l = 13L;
          |        System.out.println(bool);
          |        System.out.println(b);
          |        System.out.println(s);
          |
          |        byte b2 = b;
          |        System.out.println(b2);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(true, 10, 11, 10))
    }
  }

  describe("Language features:") {
    describe("Flow control:") {
      it("handles if-else statements with literals") {
        val src =
          """class Main {
            |    public static void main() {
            |        if (true) System.out.println("true 1");
            |        else System.out.println("false 1");
            |        if (false) System.out.println("true 2");
            |        else System.out.println("false 2");
            |    }
            |}""".stripMargin
        assertRun(src, Seq("true 1", "false 2"))
      }

      it("handles if-else statements with conditions") {
        val src =
          """class Main {
            |    static String keiko = "cat";
            |    static String bella = "dog";
            |    public static void main() {
            |        if (keiko == "cat")
            |            System.out.println("Keiko is a cat");
            |        if (keiko == "dog")
            |            System.out.println("Keiko is a dog");
            |        if (keiko == "cat" || bella == "cat")
            |            System.out.println("I have at least one cat");
            |        if (keiko == "cat" && bella == "dog")
            |            System.out.println("I have a cat and a dog");
            |    }
            |}""".stripMargin
        assertRun(src, Seq("Keiko is a cat", "I have at least one cat",
          "I have a cat and a dog"))
      }

      it("handles if-elseif-else statements") {
        val src =
          """class Main {
            |    public static void main() {
            |        if (false) System.out.println("one");
            |        else if (true) System.out.println("two");
            |        else System.out.println("three");
            |    }
            |}""".stripMargin
        assertRun(src, "two")
      }

      it("handles ternary expressions") {
        val src =
          """class Main {
            |    public static void main() {
            |        int a = true ? 10 : 20;
            |        int b = new Object() == new Object() ? 30 : 40;
            |        String animal = "dog";
            |        String coolness = animal == "cat" ? "cool" : "uncool";
            |        System.out.println(a);
            |        System.out.println(b);
            |        System.out.println(coolness);
            |    }
            |}""".stripMargin
        assertRun(src, Seq(10, 40, "uncool"));
      }

      it("handles simple for loops") {
        val src =
          """class Main {
            |    public static void main() {
            |        for (int i = 0; i < 10; i++) {
            |            System.out.println(i);
            |        }
            |    }
            |}""".stripMargin
        assertRun(src, Seq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
      }

      it("breaks and continues for loops") {
        val src =
          """class Main {
            |    public static void main() {
            |        for (int i = 0; i < 10; i++) {
            |            if (i % 2 == 0) continue;
            |            if (i > 6) break;
            |            System.out.println(i);
            |        }
            |    }
            |}""".stripMargin
        assertRun(src, Seq(1, 3, 5))
      }

      it("supports do-while") {
        val src =
          """class Main {
            |    public static void main() {
            |        do {
            |            System.out.println("a");
            |        } while (false);
            |        int i = 0;
            |        do {
            |            System.out.println(i++);
            |        } while (i < 5);
            |    }
            |}""".stripMargin
        assertRun(src, Seq("a", 0, 1, 2, 3, 4))
      }

      it("breaks and continues do-while") {
        val src =
          """class Main {
            |    public static void main() {
            |        int i = 0;
            |        do {
            |            i++;
            |            if (i % 2 == 0) continue;
            |            if (i > 10) break;
            |            System.out.println(i);
            |        } while (i < 20);
            |    }
            |}""".stripMargin
        assertRun(src, Seq(1, 3, 5, 7, 9))
      }

      //      it("supports enhanced for loops") {
      //        val src =
      //          """class Main {
      //            |    public static void main() {
      //            |        int[] arr = { 1, 2, 3, 4, 5 };
      //            |        for (int x : arr) {
      //            |            //System.out.println(x);
      //            |        }
      //            |    }
      //            |}""".stripMargin
      //        assertRun(src, Seq(1, 2, 3, 4, 5))
      //      }

      //      it("supports throwing exceptions") {
      //        val src =
      //          """class Main {
      //            |    public static void main() {
      //            |        throw new Exception("foo");
      //            |    }
      //            |}""".stripMargin
      //        assertRun(src, "bar")
      //      }
    }

    it("handles literals") {
      val src =
        """class Main {
          |    public static void main() {
          |        System.out.println(10); // int
          |        System.out.println(11L); // long
          |        System.out.println(12.5f); // float
          |        System.out.println(13.5d); // double
          |        System.out.println(true);
          |        System.out.println('@');
          |    }
          |}""".stripMargin
      assertRun(src, Seq(10, 11, 12.5, 13.5, true, '@'))
    }

    it("initializes variables with their zero value") {
      val src =
        """class Main {
          |    static boolean bool;
          |    static char c;
          |    static byte b;
          |    static short s;
          |    static int i;
          |    static long l;
          |    static float f;
          |    static double d;
          |    static Object o;
          |    static String str;
          |    public static void main() {
          |        System.out.println(bool);
          |        System.out.println(c);
          |        System.out.println(b);
          |        System.out.println(s);
          |        System.out.println(i);
          |        System.out.println(l);
          |        System.out.println(f);
          |        System.out.println(d);
          |        System.out.println(o);
          |        System.out.println(str);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(false, "\u0000", 0, 0, 0, 0, 0, 0, "null", "null"))
    }

    it("does math") {
      val src =
        """class Main {
          |    public static void main() {
          |        System.out.println(10 + 20);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(30))
    }

    it("increments variables") {
      val src =
        """class Main {
          |    public static void main() {
          |        byte b = 10;
          |        char c = 'a';
          |        short s = 20;
          |        int i = 30;
          |        long l = 40;
          |        float f = 50f;
          |        double d = 60.0;
          |        System.out.println(b++);
          |        System.out.println(c++);
          |        System.out.println(s++);
          |        System.out.println(i++);
          |        System.out.println(l++);
          |        System.out.println(f++);
          |        System.out.println(d++);
          |        System.out.println(b);
          |        System.out.println(c);
          |        System.out.println(s);
          |        System.out.println(i);
          |        System.out.println(l);
          |        System.out.println(f);
          |        System.out.println(d);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(10, "a", 20, 30, 40, 50, 60,
        11, "b", 21, 31, 41, 51, 61))
    }

    it("decrements variables") {
      val src =
        """class Main {
          |    public static void main() {
          |        byte b = 10;
          |        char c = 'a';
          |        short s = 20;
          |        int i = 30;
          |        long l = 40;
          |        float f = 50f;
          |        double d = 60.0;
          |        System.out.println(b--);
          |        System.out.println(c--);
          |        System.out.println(s--);
          |        System.out.println(i--);
          |        System.out.println(l--);
          |        System.out.println(f--);
          |        System.out.println(d--);
          |        System.out.println(b);
          |        System.out.println(c);
          |        System.out.println(s);
          |        System.out.println(i);
          |        System.out.println(l);
          |        System.out.println(f);
          |        System.out.println(d);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(10, "a", 20, 30, 40, 50, 60,
        9, "`", 19, 29, 39, 49, 59))
    }

    it("prefix increments variables") {
      val src =
        """class Main {
          |    public static void main() {
          |        byte b = 10;
          |        char c = 'a';
          |        short s = 20;
          |        int i = 30;
          |        long l = 40;
          |        float f = 50f;
          |        double d = 60.0;
          |        System.out.println(++b);
          |        System.out.println(++c);
          |        System.out.println(++s);
          |        System.out.println(++i);
          |        System.out.println(++l);
          |        System.out.println(++f);
          |        System.out.println(++d);
          |        System.out.println(b);
          |        System.out.println(c);
          |        System.out.println(s);
          |        System.out.println(i);
          |        System.out.println(l);
          |        System.out.println(f);
          |        System.out.println(d);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(11, "b", 21, 31, 41, 51, 61,
        11, "b", 21, 31, 41, 51, 61))
    }

    it("prefix decrements variables") {
      val src =
        """class Main {
          |    public static void main() {
          |        byte b = 10;
          |        char c = 'a';
          |        short s = 20;
          |        int i = 30;
          |        long l = 40;
          |        float f = 50f;
          |        double d = 60.0;
          |        System.out.println(--b);
          |        System.out.println(--c);
          |        System.out.println(--s);
          |        System.out.println(--i);
          |        System.out.println(--l);
          |        System.out.println(--f);
          |        System.out.println(--d);
          |        System.out.println(b);
          |        System.out.println(c);
          |        System.out.println(s);
          |        System.out.println(i);
          |        System.out.println(l);
          |        System.out.println(f);
          |        System.out.println(d);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(9, "`", 19, 29, 39, 49, 59,
        9, "`", 19, 29, 39, 49, 59))
    }

    it("prefix complements booleans") {
      val src =
        """class Main {
          |    public static void main() {
          |        boolean bt = true;
          |        boolean bf = false;
          |        Boolean Bt = true;
          |        Boolean Bf = false;
          |        System.out.println(!bt);
          |        System.out.println(!bf);
          |        System.out.println(!Bt);
          |        System.out.println(!Bf);
          |        System.out.println(bt);
          |        System.out.println(bf);
          |        System.out.println(Bt);
          |        System.out.println(Bf);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(false, true, false, true,
        true, false, true, false))
    }

    it("prefix complements integral primitives") {
      val src =
        """class Main {
          |    public static void main() {
          |        byte b = 10;
          |        char c = 'a';
          |        short s = 20;
          |        int i = 30;
          |        long l = 40;
          |        System.out.println(~b);
          |        System.out.println(~c);
          |        System.out.println(~s);
          |        System.out.println(~i);
          |        System.out.println(~l);
          |        System.out.println(b);
          |        System.out.println(c);
          |        System.out.println(s);
          |        System.out.println(i);
          |        System.out.println(l);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(-11, -98, -21, -31, -41,
        10, "a", 20, 30, 40))
    }

    // TODO: Test arithmetic operators with boxed classes
    //  Do this by dynamically generating test cases

    it("prefix negates primitives") {
      val src =
        """class Main {
          |    public static void main() {
          |        byte b = 10;
          |        char c = 'a';
          |        short s = 20;
          |        int i = 30;
          |        long l = 40;
          |        float f = 50f;
          |        double d = 60.0;
          |        System.out.println(-b);
          |        System.out.println(-c);
          |        System.out.println(-s);
          |        System.out.println(-i);
          |        System.out.println(-l);
          |        System.out.println(-f);
          |        System.out.println(-d);
          |        System.out.println(b);
          |        System.out.println(c);
          |        System.out.println(s);
          |        System.out.println(i);
          |        System.out.println(l);
          |        System.out.println(f);
          |        System.out.println(d);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(-10, -97, -20, -30, -40, -50, -60,
        10, "a", 20, 30, 40, 50, 60))
    }

    it("prefix pluses primitives") {
      val src =
        """class Main {
          |    public static void main() {
          |        byte b = 10;
          |        char c = 'a';
          |        short s = 20;
          |        int i = 30;
          |        long l = 40;
          |        float f = 50f;
          |        double d = 60.0;
          |        System.out.println(+b);
          |        System.out.println(+c);
          |        System.out.println(+s);
          |        System.out.println(+i);
          |        System.out.println(+l);
          |        System.out.println(+f);
          |        System.out.println(+d);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(10, 97, 20, 30, 40, 50, 60))
    }

    it("supports extended operands") {
      val src =
        """class Main {
          |    public static void main() {
          |        System.out.println(1 + 2 + 3 + 4 + 5 + 6);
          |        System.out.println(1 * 2 * 3 * 4 * 5 * 6);
          |        System.out.println("a" + "b" + "c" + 10);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(21, 720, "abc10"))
    }

    it("supports arithmetic assigns") {
      val src =
        """class Main {
          |    public static void main() {
          |        int i = 10;
          |        i += 10; System.out.println(i);
          |        i -= 4; System.out.println(i);
          |        i *= 4; System.out.println(i);
          |        i /= 2; System.out.println(i);
          |        i <<= 2; System.out.println(i);
          |        i >>= 2; System.out.println(i);
          |        i *= -1; i >>= 2; System.out.println(i);
          |        i >>>= 1; System.out.println(i);
          |        i = 0xaaaa;
          |        i &= 0xabcd; System.out.println(i);
          |        i |= 0x1234; System.out.println(i);
          |        i ^= 0xffff; System.out.println(i);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(20, 16, 64, 32, 128, 32, -8, 2147483644,
        43656, 47804, 17731));
    }

    it("assignment returns assigned value") {
      val src =
        """class Main {
          |    public static void main() {
          |        int i = 10;
          |        int j = i = 15;
          |        System.out.println(i);
          |        System.out.println(j);
          |        System.out.println(i = 20);
          |        System.out.println(i);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(15, 15, 20, 20));
    }

    it("assignment returns assigned value and does not duplicate side-effect") {
      val src =
        """class Main {
          |    static int value() { System.out.println("value"); return 20; }
          |    public static void main() {
          |        int i = 10;
          |        int j = i = value();
          |        System.out.println(i);
          |        System.out.println(j);
          |    }
          |}""".stripMargin
      assertRun(src, Seq("value", 20, 20));
    }

    it("assignments can be chained with local and static variables") {
      val src =
        """class Main {
          |    static int si;
          |    static int sj;
          |    public static void main() {
          |        int i = 0; int j = 0;
          |        i = si = j = sj = 10;
          |        System.out.println(i); System.out.println(j);
          |        System.out.println(si); System.out.println(sj);
          |        si = i = sj = j = 20;
          |        System.out.println(i); System.out.println(j);
          |        System.out.println(si); System.out.println(sj);
          |        si = sj = i = j = 30;
          |        System.out.println(i); System.out.println(j);
          |        System.out.println(si); System.out.println(sj);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(10, 10, 10, 10, 20, 20, 20, 20, 30, 30, 30, 30));
    }

    it("assignments can be chained in vardefs") {
      val src =
        """class Main {
          |    static int si = 10;
          |    static int sj = si = 20;
          |    public static void main() {
          |        int i = 30;
          |        int j = i = 40;
          |        System.out.println(i); System.out.println(j);
          |        System.out.println(si); System.out.println(sj);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(40, 40, 20, 20));
    }

    it("supports type literals (.class)") {
      val src =
        """class Main{
          |    public static void main() {
          |        System.out.println(void.class);
          |        System.out.println(int.class);
          |        System.out.println(String.class);
          |        Class c1 = String.class;
          |        Class<String> c2 = String.class;
          |        Class<? extends Number> c3 = Integer.class;
          |        System.out.println(c1);
          |        System.out.println(c2);
          |        System.out.println(c3);
          |    }
          |}""".stripMargin
      assertRun(src, Seq("void", "int",
        "class java.lang.String", "class java.lang.String",
        "class java.lang.String", "class java.lang.Integer"))
    }
  }

  describe("Instances:") {
    it("constructs JDK objects") {
      val src =
        """import java.util.Random;
          |class Main {
          |    public static void main() {
          |        String x = new String("A string");
          |        System.out.println(x);
          |        Random r = new Random(1234);
          |        System.out.println(r.nextInt(10));
          |        Object o = new Object();
          |        System.out.println(o.toString());
          |        Integer i = new Integer(10);
          |        System.out.println(new Integer(i.compareTo(30)));
          |    }
          |}""".stripMargin
      assertRun(src, Seq("A string", 8, "java.lang.Object@1", -1))
    }

    it("constructs objects") {
      val src =
        """class Main {
          |    public static void main() {
          |        String x = new String("A string");
          |        System.out.println(x);
          |    }
          |}""".stripMargin
      assertRun(src, "A string")
    }

    it("declares instance fields") {
      val src =
        """class Main {
          |    public int i = 10;
          |    public String s = "Instance string";
          |    public static void main() {
          |        Main m = new Main();
          |        System.out.println(m.i);
          |        System.out.println(m.s);
          |    }
          |}
          |""".stripMargin
      assertRun(src, Seq(10, "Instance string"))
    }

    it("sets instance fields") {
      val src =
        """class Main {
          |    public int i = 10;
          |    public String s = "Instance string";
          |    public static void main() {
          |        Main m = new Main();
          |        System.out.println(m.i);
          |        System.out.println(m.s);
          |        m.i = 20;
          |        m.s = "Changed string";
          |        System.out.println(m.i);
          |        System.out.println(m.s);
          |    }
          |}
          |""".stripMargin
      assertRun(src, Seq(10, "Instance string", 20, "Changed string"))
    }

    it("calls instance methods") {
      val src =
        """class Main {
          |    public int inst() {
          |        System.out.println("inst");
          |        return 10;
          |    }
          |
          |    public static void main() {
          |        Main m = new Main();
          |        System.out.println("main");
          |        System.out.println(m.inst());
          |    }
          |}
          |""".stripMargin
      assertRun(src, Seq("main", "inst", 10))
    }

    it("calls different constructors") {
      val src =
        """class Main {
          |    public static void main() {
          |        String s1 = new String("from literal");
          |        String s2 = new String(s1);
          |        String s3 = new String(new byte[] { 98, 121, 116, 101, 115 });
          |        String s4 = new String(new char[] { 'c', 'h', 'a', 'r', 's' });
          |        System.out.println(s1);
          |        System.out.println(s2);
          |        System.out.println(s3);
          |        System.out.println(s4);
          |    }
          |}
          |""".stripMargin
      assertRun(src, Seq("from literal", "from literal", "bytes", "chars"))
    }

    it("calls a custom top-level constructor") {
      val src =
        """
          |class Main {
          |    int field = 10;
          |    int field2 = 20;
          |    public Main(int i) {
          |        this.field2 = i;
          |        System.out.println("zero");
          |    }
          |    public static void main() {
          |       Main a = new Main(30);
          |       System.out.println(a.field);
          |       System.out.println(a.field2);
          |    }
          |}""".stripMargin
      assertRun(src, Seq("zero", 10, 30))
    }

    it("calls a custom top-level constructor calling implicit super()") {
      val src =
        """
          |class Main {
          |    int field = 10;
          |    int field2 = 20;
          |    public Main(int i) {
          |        super();
          |        this.field2 = i;
          |        System.out.println("one");
          |    }
          |    public static void main() {
          |       Main a = new Main(30);
          |       System.out.println(a.field);
          |       System.out.println(a.field2);
          |    }
          |}""".stripMargin
      assertRun(src, Seq("one", 10, 30))
    }

    it("calls a custom top-level constructor calling co-constructor") {
      val src =
        """
          |class Main {
          |    int field = 10;
          |    int field2 = 20;
          |    int field3 = 30;
          |    public Main(int i, int j) {
          |        this(i);
          |        this.field3 = j;
          |    }
          |    public Main(int i) {
          |        this.field2 = i;
          |        System.out.println("zero");
          |    }
          |    public static void main() {
          |       Main a = new Main(30, 40);
          |       System.out.println(a.field);
          |       System.out.println(a.field2);
          |       System.out.println(a.field3);
          |    }
          |}""".stripMargin
      assertRun(src, Seq("zero", 10, 30, 40))
    }

    it("implicitly calls a super constructor") {
      val src =
        """class Other {
          |    public Other() {
          |        System.out.println("other");
          |    }
          |}
          |class Main extends Other {
          |    public Main() {
          |        System.out.println("main");
          |    }
          |
          |    public static void main() {
          |        Main main = new Main();
          |    }
          |}
          |
          |""".stripMargin
      assertRun(src, Seq("other", "main"))
    }

    it("explicitly calls a super constructor") {
      val src =
        """class Other {
          |    public Other() {
          |        System.out.println("other");
          |    }
          |}
          |class Main extends Other {
          |    public Main() {
          |        super();
          |        System.out.println("main");
          |    }
          |
          |    public static void main() {
          |        Main main = new Main();
          |    }
          |}
          |
          |""".stripMargin
      assertRun(src, Seq("other", "main"))
    }

    it("explicitly calls an overloaded super constructor") {
      val src =
        """class Other {
          |    public Other() {
          |        System.out.println("other 0");
          |    }
          |    public Other(String str) {
          |        System.out.println("other 1");
          |        System.out.println(str);
          |    }
          |}
          |class Main extends Other {
          |    public Main() {
          |        super("arg");
          |        System.out.println("main");
          |    }
          |
          |    public static void main() {
          |        Main main = new Main();
          |    }
          |}
          |
          |""".stripMargin
      assertRun(src, Seq("other 1", "arg", "main"))
    }

    it("accesses members from other custom classes") {
      val src =
        """class Other {
          |    public int number = 3;
          |    public int method(int x) {
          |         System.out.println("Other#method");
          |         System.out.println(x);
          |         return 10;
          |     }
          |}
          |class Main {
          |    public static void main() {
          |        Other other = new Other();
          |        System.out.println(other.number);
          |        other.number = 4;
          |        System.out.println(other.number);
          |        System.out.println(other.method(5));
          |    }
          |}
          |
          |""".stripMargin
      assertRun(src, Seq(3, 4, "Other#method", 5, 10))
    }

    it("inherits stuff") {
      val src =
        """class Base {
          |    public String cat = "cat";
          |    public String dog = "dog";
          |    public String say() { return "meow"; }
          |}
          |class Main extends Base {
          |    public String dog = "poodle";
          |    public String say() { return "bark"; }
          |    public static void main() {
          |        Base b = new Base();
          |        System.out.println(b.cat);
          |        System.out.println(b.dog);
          |        System.out.println(b.say());
          |        Main m = new Main();
          |        System.out.println(m.cat);
          |        System.out.println(m.dog);
          |        System.out.println(m.say());
          |        Base mb = new Main();
          |        System.out.println(mb.cat);
          |        System.out.println(mb.dog);
          |        System.out.println(mb.say());
          |    }
          |}""".stripMargin
      assertRun(src, Seq(
        "cat", "dog", "meow",
        "cat", "poodle", "bark",
        "cat", "dog", "bark"
      ))
    }

    it("calls super fields and methods") {
      val src =
        """class A {
          |    public String field = "fieldA";
          |    public String method() { return "methodA"; }
          |    public String superOnlyMethod() { return "superOnly"; }
          |}
          |class B extends A {
          |    public String field = "fieldB";
          |    public String method() { return "methodB"; }
          |    public String superField() { return super.field; }
          |    public String superMethod() { return super.method(); }
          |    public String callSuperOnlyMethod() { return superOnlyMethod(); }
          |}
          |class Main {
          |    public static void main() {
          |        B b = new B();
          |        System.out.println(b.field);
          |        System.out.println(b.method());
          |        System.out.println(b.superField());
          |        System.out.println(b.superMethod());
          |        System.out.println(b.superOnlyMethod());
          |        System.out.println(b.callSuperOnlyMethod());
          |    }
          |}
          |""".stripMargin
      assertRun(src, Seq("fieldB", "methodB", "fieldA", "methodA", "superOnly",
        "superOnly"))
    }
  }

  describe("Static fields") {
    it("are initialized and can be read") {
      val src =
        """class Main {
          |    static String stat = "A static field";
          |    public static void main() {
          |        System.out.println(stat);
          |    }
          |}""".stripMargin
      assertRun(src, "A static field")
    }

    it("are initialized and can be written") {
      val src =
        """class Main {
          |    static String stat = "A static field";
          |    public static void main() {
          |        System.out.println(stat);
          |        stat = "A changed static field";
          |        System.out.println(stat);
          |    }
          |}""".stripMargin
      assertRun(src, Seq("A static field", "A changed static field"))
    }

    it("can be accessed with fully qualified names") {
      val src =
        """class Main {
          |    static String stat = "A static field";
          |    public static void main() {
          |        System.out.println(Main.stat);
          |        Main.stat = "A changed static field";
          |        System.out.println(Main.stat);
          |    }
          |}""".stripMargin
      assertRun(src, Seq("A static field", "A changed static field"))
    }

    it("support all primitive types") {
      // TODO: This test needs to support implicit coercion of byte->int and short->int
      val src =
        """class Main {
          |    static byte b = 100;
          |    static short s = 101;
          |    static int i = 102;
          |    static long l = 103;
          |    static float f = 104.5f;
          |    static double d = 105.5;
          |    static boolean bool = true;
          |    static char c = '@';
          |    public static void main() {
          |        System.out.println((int) b);
          |        System.out.println((int) s);
          |        System.out.println(i);
          |        System.out.println(l);
          |        System.out.println(f);
          |        System.out.println(d);
          |        System.out.println(bool);
          |        System.out.println(c);
          |    }
          |}""".stripMargin
      assertRun(src, Seq(100, 101, 102, 103, 104.5, 105.5, true, '@'))
    }
  }

  describe("Static methods") {
    it("can be defined and called") {
      val src =
        """class Main {
          |    static void one() {
          |        System.out.println("one");
          |    }
          |    static void two() {
          |        System.out.println("two");
          |    }
          |    public static void main() {
          |        System.out.println("main");
          |        one();
          |        two();
          |    }
          |}""".stripMargin
      assertRun(src, Seq("main", "one", "two"))
    }

    it("can be defined an called with fully qualified names") {
      val src =
        """class Main {
          |    static void one() {
          |        System.out.println("one");
          |    }
          |    static void two() {
          |        System.out.println("two");
          |    }
          |    public static void main() {
          |        System.out.println("main");
          |        Main.one();
          |        Main.two();
          |    }
          |}""".stripMargin
      assertRun(src, Seq("main", "one", "two"))
    }

    it("can return values") {
      val src =
        """class Main {
          |    static String makeString() {
          |        return "A string";
          |    }
          |    public static void main() {
          |        System.out.println(makeString());
          |        System.out.println(Main.makeString());
          |    }
          |}""".stripMargin
      assertRun(src, Seq("A string", "A string"))
    }

    it("have side-effects") {
      val src =
        """class Main {
          |    static String stat = "A string";
          |    static void modify() {
          |        stat = "A modified string";
          |    }
          |    public static void main() {
          |        System.out.println(stat);
          |        modify();
          |        System.out.println(stat);
          |    }
          |}""".stripMargin
      assertRun(src, Seq("A string", "A modified string"))
    }
  }

  describe("Classes") {
    it("defines an inner class") {
      val src =
        """class Main {
          |    class Inner {
          |        static final String constant = "constant";
          |        String instance = "instance";
          |        String method() {
          |            return "method";
          |        }
          |    }
          |
          |    public Main() {
          |        System.out.println(Inner.constant);
          |        System.out.println(new Inner().instance);
          |        System.out.println(new Inner().method());
          |    }
          |
          |    public static void main() {
          |        new Main();
          |    }
          |}""".stripMargin
      assertRun(src, Seq("constant", "instance", "method"))
    }

    it("calls outer methods from inner classes") {
      val src =
        """class Main {
          |    class Inner1 {
          |        class Inner2 {
          |            public Inner2() {
          |                System.out.println("inner2");
          |                inner1Print();
          |                mainPrint();
          |            }
          |        }
          |        public Inner1() {
          |            new Inner2();
          |        }
          |        public void inner1Print() {
          |            System.out.println("inner1");
          |            mainPrint();
          |        }
          |    }
          |    public void mainPrint() {
          |        System.out.println("main");
          |    }
          |    Main() { new Inner1(); }
          |    public static void main() {
          |        new Main();
          |    }
          |}""".stripMargin
      assertRun(src, Seq("inner2", "inner1", "main", "main"))
    }

    it("inherits from inner classes with different scopes") {
      val src =
        """class Main {
          |    void print() { System.out.println("Main"); }
          |    class Base {
          |        public Base() { new Inner(); print(); }
          |        class Inner {
          |            void print() { System.out.println("Inner"); }
          |            class Sub { public Sub() { print(); } }
          |            public Inner() { new Sub(); }
          |        }
          |    }
          |    public Main() { new Base(); }
          |    public static void main() { new Main(); }
          |}""".stripMargin
      assertRun(src, Seq("Inner", "Main"))
    }

    it("passes outer scope through through implicit constructors") {
      val src =
        """class Main {
          |    int a = 10;
          |    int b = 20;
          |    class A { void printA() { System.out.println(a); } }
          |    class B extends A { void printB() { System.out.println(b); }}
          |    public Main() {
          |        B b = new B();
          |        b.printA();
          |        b.printB();
          |    }
          |    public static void main() { new Main(); }
          |}""".stripMargin
      assertRun(src, Seq(10, 20))
    }

    it("passes outer scope through through explicit constructors") {
      val src =
        """class Main {
          |    int a = 10;
          |    int b = 20;
          |    class A {
          |        public A() { System.out.println("construct A"); }
          |        void printA() { System.out.println(a); }
          |    }
          |    class B extends A {
          |        public B() { System.out.println("construct B"); }
          |        void printB() { System.out.println(b); }
          |    }
          |    public Main() {
          |        B b = new B();
          |        b.printA();
          |        b.printB();
          |    }
          |    public static void main() { new Main(); }
          |}""".stripMargin
      assertRun(src, Seq("construct A", "construct B", 10, 20))
    }

    it("passes outer scope through through explicit constructors with params") {
      val src =
        """class Main {
          |    int a = 10;
          |    int b = 20;
          |    class A {
          |        public A(String s) { System.out.println(s); }
          |        void printA() { System.out.println(a); }
          |    }
          |    class B extends A {
          |        public B(String s) { super("construct A"); System.out.println(s); }
          |        void printB() { System.out.println(b); }
          |    }
          |    public Main() {
          |        B b = new B("construct B");
          |        b.printA();
          |        b.printB();
          |    }
          |    public static void main() { new Main(); }
          |}""".stripMargin
      assertRun(src, Seq("construct A", "construct B", 10, 20))
    }

    it("passes outer scope through through explicit co-constructors with params") {
      val src =
        """class Main {
          |    int a = 10;
          |    int b = 20;
          |    class A {
          |        public A(String s) { System.out.println(s); }
          |        void printA() { System.out.println(a); }
          |    }
          |    class B extends A {
          |        public B() { super("construct A"); }
          |        public B(String s) { this(); System.out.println(s); }
          |        void printB() { System.out.println(b); }
          |    }
          |    public Main() {
          |        B b = new B("construct B");
          |        b.printA();
          |        b.printB();
          |    }
          |    public static void main() { new Main(); }
          |}""".stripMargin
      assertRun(src, Seq("construct A", "construct B", 10, 20))
    }
  }

  describe("Generics") {
    it("calls generic methods on JDK classes") {
      val src =
        """import java.util.LinkedList;
          |class Main {
          |    static {
          |         System.out.println("static");
          |    }
          |
          |    public static void main() {
          |        LinkedList<Integer> ints = new LinkedList<>();
          |        LinkedList<String> strings = new LinkedList<>();
          |        ints.add(10);
          |        ints.add(20);
          |        strings.add("one");
          |        strings.add("two");
          |        System.out.println(ints.get(0));
          |        System.out.println(ints.get(1));
          |        System.out.println(strings.get(0));
          |        System.out.println(strings.get(1));
          |    }
          |}""".stripMargin
      assertRun(src, Seq(10, 20, "one", "two"))
    }

    it("defines and uses a generic class") {
      val src =
        """class Container<T> {
          |    T value;
          |    public Container(T value) { set(value); }
          |    public T get() { return value; }
          |    public void set(T value) { this.value = value; }
          |}
          |
          |class Main {
          |    public static void main() {
          |        Container<String> s = new Container<String>("foo");
          |        System.out.println(s.get());
          |        s.set("bar");
          |        System.out.println(s.get());
          |        System.out.println(s.value);
          |
          |        Container<Integer> i = new Container<Integer>(10);
          |        System.out.println(i.get());
          |    }
          |}""".stripMargin
      assertRun(src, Seq("foo", "bar", "bar", 10))
    }

    it("defines and uses a generic class with custom data") {
      val src =
        """class Container<T> {
          |    T value;
          |    public Container(T value) { set(value); }
          |    public T get() { return value; }
          |    public void set(T value) { this.value = value; }
          |}
          |
          |class Data {}
          |
          |class Main {
          |    public static void main() {
          |        Data data1 = new Data();
          |        Data data2 = new Data();
          |        Container<Data> c = new Container<Data>(data1);
          |        System.out.println(c.get());
          |        c.set(data2);
          |        System.out.println(c.get());
          |        System.out.println(c.value);
          |    }
          |}""".stripMargin
      assertRun(src, Seq("test.Data@1", "test.Data@2", "test.Data@2"))
    }

    it("supports static parameterized methods") {
      val src =
        """class Main {
          |    static <T> T[] setHead(T[] arr, T item) { arr[0] = item; return arr; }
          |    public static void main() {
          |        Object[] arr = new Object[] { new Object(), new Object() };
          |        System.out.println(arr[0].hashCode());
          |        System.out.println(arr[1].hashCode());
          |        arr = setHead(arr, new Object());
          |        System.out.println(arr[0].hashCode());
          |        System.out.println(arr[1].hashCode());
          |    }
          |}""".stripMargin
      assertRun(src, Seq(1, 2, 3, 2))
    }

    it("supports instance parameterized methods") {
      val src =
        """class Main {
          |    <T> T[] setHead(T[] arr, T item) { arr[0] = item; return arr; }
          |    public void test() {
          |        Object[] arr = new Object[] { new Object(), new Object() };
          |        System.out.println(arr[0].hashCode());
          |        System.out.println(arr[1].hashCode());
          |        arr = setHead(arr, new Object());
          |        System.out.println(arr[0].hashCode());
          |        System.out.println(arr[1].hashCode());
          |    }
          |    public static void main() {
          |        new Main().test();
          |    }
          |}""".stripMargin
      assertRun(src, Seq(1, 2, 3, 2))
    }
  }

  it("support switch statements") {
    val src =
      """class Main {
        |    public static void main() {
        |        for (int i = 0; i < 4; i++) {
        |            switch (i % 2) {
        |                case 0:
        |                    System.out.println("even");
        |                    break;
        |                case 1:
        |                    System.out.println("odd");
        |                default:
        |                    System.out.println("fallthrough");
        |            }
        |        }
        |    }
        |}""".stripMargin
    assertRun(src, Seq("even", "odd", "fallthrough", "even", "odd", "fallthrough"))
  }

  it("supports array length") {
    val src =
      """class Main {
        |    static int[] one = new int[1];
        |    int[] two = new int[2];
        |    static int[] many(int len) { return new int[999]; }
        |    public static void main() {
        |        int[] zero = new int[0];
        |        int[] looped = new int[] { 0, 1, 2, 3, 4, 5 };
        |        System.out.println(zero.length);
        |        System.out.println(one.length);
        |        System.out.println(new Main().two.length);
        |        System.out.println(many(999).length);
        |        for (int i = 0; i < looped.length; i++) {
        |            System.out.println(looped[i]);
        |        }
        |    }
        |}""".stripMargin
    assertRun(src, Seq(0, 1, 2, 999, 0, 1, 2, 3, 4, 5))
  }

  it("breaks out of named for loops") {
    val src =
      """class Main {
        |    public static void main() {
        |        OUTER: for (int i = 0; true; i++) {
        |            for (int j = 0; true; j++) {
        |                System.out.println(j);
        |                if (i == 3) break OUTER;
        |                if (j == 3) continue OUTER;
        |            }
        |        }
        |    }
        |}""".stripMargin
    assertRun(src, Seq(0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0))
  }

  it("executes static initializers and constructors in the right order") {
    val src =
      """class Base {
        |    static { System.out.println("Base static"); }
        |    public Base() { System.out.println("Base init"); }
        |}
        |class Sub extends Base {
        |    static { System.out.println("Sub static"); }
        |    public Sub() { System.out.println("Sub init"); }
        |}
        |class Main {
        |    public static void main() {
        |        new Sub();
        |    }
        |}""".stripMargin
    assertRun(src, Seq("Base static", "Base init", "Sub static", "Sub init"))
  }

//  it("sandbox") {
//    val src =
//      """
//        |class Base {
//        |    static { System.out.println("Base static"); }
//        |}
//        |class Sub extends Base {
//        |    static { System.out.println("Sub static"); }
//        |}
//        |class Main {
//        |    public static void main() {
//        |        new Sub();
//        |    }
//        |}
//        |""".stripMargin
//    assertRun(src, Seq("SJS-JFE says:", "Hello", "Scala.js"))
//  }

  // TODO: infix ogical condition short-circuiting. Is left-fold correct?
  // TODO: Test NPEs in selects
}
