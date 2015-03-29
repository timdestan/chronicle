module LastFmImporterTests

open LastFmImporter

open NUnit.Framework
open FsUnit
open Microsoft.FSharp.Control

// Actual last.fm user to test on 
let testUserName = "tj6186"
// Lower bound on the number of tracks the test user has scrobbled.
let tracksPlayedLowerBound = 26000

[<Test>]
let ``Can get a user`` () =
    let user = LastFmImporter.getUser testUserName |> Async.RunSynchronously
    user.name |> should equal testUserName
    user.trackCount |> should be (greaterThan tracksPlayedLowerBound)

[<Test>]
let ``Can get top tracks for a user`` () =
    let tracks = LastFmImporter.getTopTracksForUser testUserName |> Async.RunSynchronously |> Seq.toArray
    tracks.Length |> should be (greaterThan 10)

[<Test>]
[<Timeout(600000)>]
let ``Can get all listens for a user eventually`` () =
    let tracks = LastFmImporter.getAllListensForUser testUserName |> Async.RunSynchronously |> Seq.toArray
    tracks.Length |> should be (greaterThanOrEqualTo tracksPlayedLowerBound)
