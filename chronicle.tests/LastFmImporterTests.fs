module LastFmImporterTests

open LastFmImporter

open NUnit.Framework
open FsUnit
open Microsoft.FSharp.Control

[<Test>]
let ``Can read a user`` () =
    let user = LastFmImporter.getUser "tj6186" |> Async.RunSynchronously
    user.Name |> should equal "tj6186"
    user.TrackCount |> should be (greaterThan 26000)

[<Test>]
let ``Can read tracks for a user`` () =
    let tracks = LastFmImporter.getTracksForUser "tj6186" |> Async.RunSynchronously |> Seq.toArray
    tracks.Length |> should be (greaterThan 10)
