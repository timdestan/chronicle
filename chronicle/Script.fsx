#r "bin/Debug/chronicle.dll"

open Microsoft.FSharp.Control
open LastFmImporter

// Get my username for an example
async {
    let! user = getUser "tj6186"
    printfn "User:%s, with %d tracks played" user.Name user.TrackCount
} |> Async.RunSynchronously
