package io.atleastonce.wallet.manager.domain

import org.scalatestplus.play._

class UserSpec extends PlaySpec {
  "User" should {
    "throws an Error if id is null" in {
      a [IllegalArgumentException] must be thrownBy {
        User(null, "n1", "756fd927-4344-468c-b678-abf2ff4c2155", "sak1")
      }
    }

    "throws an Error if id is empty" in {
      a [IllegalArgumentException] must be thrownBy {
        User("", "n1", "756fd927-4344-468c-b678-abf2ff4c2155", "sak1")
      }
    }

    "throws an Error if id is not an UUID" in {
      a [IllegalArgumentException] must be thrownBy {
        User("asdqwe", "n1", "756fd927-4344-468c-b678-abf2ff4c2155", "sak1")
      }
    }

    "throws an Error if name is null" in {
      a [IllegalArgumentException] must be thrownBy {
        User("e79cf53a-eb49-449e-91e6-afa6a0831ea4", null, "756fd927-4344-468c-b678-abf2ff4c2155", "sak1")
      }
    }

    "throws an Error if name is empty" in {
      a [IllegalArgumentException] must be thrownBy {
        User("e79cf53a-eb49-449e-91e6-afa6a0831ea4", "", "756fd927-4344-468c-b678-abf2ff4c2155", "sak1")
      }
    }

    "throws an Error if accessKey is null" in {
      a [IllegalArgumentException] must be thrownBy {
        User("e79cf53a-eb49-449e-91e6-afa6a0831ea4", "n1", null, "sak1")
      }
    }

    "throws an Error if accessKey is empty" in {
      a [IllegalArgumentException] must be thrownBy {
        User("e79cf53a-eb49-449e-91e6-afa6a0831ea4", "n1", "", "sak1")
      }
    }

    "throws an Error if accessKey is not an UUID" in {
      a [IllegalArgumentException] must be thrownBy {
        User("e79cf53a-eb49-449e-91e6-afa6a0831ea4", "n1", "qwer", "sak1")
      }
    }

    "throws an Error if secretAccessKey is null" in {
      a [IllegalArgumentException] must be thrownBy {
        User("e79cf53a-eb49-449e-91e6-afa6a0831ea4", "n1", "756fd927-4344-468c-b678-abf2ff4c2155", null)
      }
    }

    "throws an Error if secretAccessKey is empty" in {
      a [IllegalArgumentException] must be thrownBy {
        User("e79cf53a-eb49-449e-91e6-afa6a0831ea4", "n1", "756fd927-4344-468c-b678-abf2ff4c2155", "")
      }
    }

    "must be created correctly if all parameters are ok" in {
      val user = User("e79cf53a-eb49-449e-91e6-afa6a0831ea4", "n1", "756fd927-4344-468c-b678-abf2ff4c2155", "lala")

      user.id mustBe "e79cf53a-eb49-449e-91e6-afa6a0831ea4"
      user.name mustBe "n1"
      user.accessKey mustBe "756fd927-4344-468c-b678-abf2ff4c2155"
      user.secretAccessKey mustBe "lala"
    }

    "must add a wallet in a user correctly" in {
      val user = User("e79cf53a-eb49-449e-91e6-afa6a0831ea4", "n1", "756fd927-4344-468c-b678-abf2ff4c2155", "lala")
      val wallet = Wallet("c92c894a-7c02-4ac0-bab9-34b739274471", 1000F)

      user.addWallet(wallet) match {
        case Left(newUser) =>
          newUser.wallets.length mustBe 1
          newUser.wallets.head.id mustBe "c92c894a-7c02-4ac0-bab9-34b739274471"
          newUser.wallets.head.credit mustBe 1000F
        case _ =>
      }
    }

    "should generate a sha-256 hash" in {
      val hash = User.generateSecretKey

      hash.length mustBe 64
    }
  }
}
